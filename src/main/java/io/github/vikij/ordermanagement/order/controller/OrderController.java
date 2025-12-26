package io.github.vikij.ordermanagement.order.controller;

import io.github.vikij.ordermanagement.order.dto.*;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * USER -> own orders
     * ADMIN -> all orders
     */
    @GetMapping
    public List<OrderResponse> getOrders(Authentication authentication) {

        return orderService.getOrders(authentication).stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request,
                                     Authentication authentication) {

        Order saved = orderService.createOrder(request, authentication);
        return toOrderResponse(saved);
    }

    /**
     * Public-safe endpoint
     */
    @GetMapping("/{orderNumber}/status")
    public OrderStatusResponse getOrderStatus(@PathVariable String orderNumber) {

        Order order = orderService.getByOrderNumber(orderNumber);

        return new OrderStatusResponse(
                order.getOrderNumber(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductCode(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList()
        );
    }


    /**
     * ADMIN only
     */
    @PatchMapping("/{orderNumber}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrderStatus(@PathVariable String orderNumber,
                                           @RequestBody UpdateOrderStatusRequest request) {

        Order updated = orderService.updateStatus(orderNumber, request.getStatus());
        return toOrderResponse(updated);
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductCode(),
                                item.getQuantity(),
                                item.getUnitPrice()
                        ))
                        .toList()
        );
    }

}
