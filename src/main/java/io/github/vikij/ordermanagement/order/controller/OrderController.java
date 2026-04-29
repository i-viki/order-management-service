package io.github.vikij.ordermanagement.order.controller;

import io.github.vikij.ordermanagement.order.dto.*;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.mapper.OrderMapper;
import io.github.vikij.ordermanagement.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    /**
     * USER -> own orders
     * ADMIN -> all orders
     */
    @GetMapping
    public Page<OrderResponse> getOrders(@PageableDefault(size = 10) Pageable pageable,
                                         Authentication authentication) {

        return orderService.getOrders(authentication, pageable)
                .map(orderMapper::toResponse);
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request,
                                     Authentication authentication) {

        log.info("Creating order for user: {}", authentication.getName());
        Order saved = orderService.createOrder(request, authentication);
        return orderMapper.toResponse(saved);
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
                                           @Valid @RequestBody UpdateOrderStatusRequest request) {

        Order updated = orderService.updateStatus(orderNumber, request.getStatus());
        return orderMapper.toResponse(updated);
    }

}
