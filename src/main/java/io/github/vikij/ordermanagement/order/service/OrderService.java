package io.github.vikij.ordermanagement.order.service;

import io.github.vikij.ordermanagement.common.exception.ResourceNotFoundException;
import io.github.vikij.ordermanagement.order.dto.CreateOrderRequest;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.entity.OrderItem;
import io.github.vikij.ordermanagement.order.entity.OrderStatus;
import io.github.vikij.ordermanagement.order.repository.OrderRepository;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import io.github.vikij.ordermanagement.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getOrders(Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return orderRepository.findAllWithItems();
            //return orderRepository.findAll();
        }

        AppUser user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findWithItemsByCreatedBy(user);
        //return orderRepository.findByCreatedBy(user);
    }

    public Order createOrder(CreateOrderRequest request, Authentication auth) {

        AppUser user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String orderNumber = "ORD-" + System.currentTimeMillis();

        BigDecimal subtotal = request.getItems().stream()
                .map(i -> i.getUnitPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal totalAmount = subtotal.add(taxAmount);

        CreateOrderRequest.DeliveryAddressRequest addr =
                request.getDeliveryAddress();

        Order order = new Order(
                orderNumber,
                subtotal,
                taxAmount,
                totalAmount,
                addr.getAddressLine(),
                addr.getCity(),
                addr.getCountry(),
                addr.getPostalCode(),
                user
        );

        request.getItems().forEach(i -> {
            OrderItem item = new OrderItem(
                    i.getProductCode(),
                    i.getQuantity(),
                    i.getUnitPrice()
            );
            order.addItem(item);
        });

        return orderRepository.save(order);
    }


    public Order updateStatus(String orderNumber, OrderStatus status) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.updateStatus(status);
        return orderRepository.save(order);
    }

    public Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumberWithItems(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

}

