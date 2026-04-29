package io.github.vikij.ordermanagement.ai.service;

import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.repository.OrderRepository;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import io.github.vikij.ordermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            You are a helpful and professional customer support assistant for our Order Management System.
            Your goal is to answer user queries about their orders using ONLY the provided order data.
            
            USER DATA:
            Name: {name}
            Email: {email}
            
            USER ORDERS:
            {orders_data}
            
            GUIDELINES:
            1. If the user asks about an order not in the list, inform them politely.
            2. Do not reveal internal IDs or system details, focus on Order Numbers and Status.
            3. If you don't know the answer or the data is missing, ask the user to contact human support.
            4. Keep responses concise and friendly.
            5. Ensure you only discuss the data provided above. This is strict for privacy (Grounding).
            """;

    public String getChatResponse(String userQuery, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> userOrders = orderRepository.findByCreatedBy(user);
        final String finalOrdersData = userOrders.isEmpty() 
                ? "No orders found for this user." 
                : userOrders.stream()
                .map(order -> String.format("- Order #%s: Status: %s, Total: %s, Created: %s",
                        order.getOrderNumber(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt()))
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TEMPLATE)
                        .param("name", user.getFirstName() + " " + user.getLastName())
                        .param("email", user.getEmail())
                        .param("orders_data", finalOrdersData))
                .user(userQuery)
                .call()
                .content();
    }
}
