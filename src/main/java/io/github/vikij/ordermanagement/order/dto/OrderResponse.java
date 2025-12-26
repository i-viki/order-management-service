package io.github.vikij.ordermanagement.order.dto;

import io.github.vikij.ordermanagement.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String orderNumber, BigDecimal totalAmount, OrderStatus status, LocalDateTime createdAt,
                            List<OrderItemResponse> items) {

}
