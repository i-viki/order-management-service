package io.github.vikij.ordermanagement.order.service;

import io.github.vikij.ordermanagement.common.exception.ResourceNotFoundException;
import io.github.vikij.ordermanagement.order.dto.CreateOrderRequest;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.entity.OrderItem;
import io.github.vikij.ordermanagement.order.entity.OrderStatus;
import io.github.vikij.ordermanagement.order.repository.OrderRepository;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import io.github.vikij.ordermanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Page<Order> getOrders(Authentication auth, Pageable pageable) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return orderRepository.findAll(pageable);
        }

        AppUser user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByCreatedBy(user, pageable);
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request, Authentication auth) {

        AppUser user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .deliveryAddress(request.getDeliveryAddress().getAddressLine())
                .deliveryCity(request.getDeliveryAddress().getCity())
                .deliveryCountry(request.getDeliveryAddress().getCountry())
                .deliveryPostalCode(request.getDeliveryAddress().getPostalCode())
                .createdBy(user)
                .build();

        request.getItems().forEach(i -> {
            OrderItem item = OrderItem.builder()
                    .productCode(i.getProductCode())
                    .quantity(i.getQuantity())
                    .unitPrice(i.getUnitPrice())
                    .build();
            order.addItem(item);
        });

        // Totals are calculated inside addItem via calculateTotals()
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(String orderNumber, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.updateStatus(status);
        return orderRepository.save(order);
    }

    public Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}

