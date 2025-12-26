package io.github.vikij.ordermanagement.order.dto;

import io.github.vikij.ordermanagement.order.entity.OrderStatus;

import java.util.List;

public record OrderStatusResponse(String orderNumber, OrderStatus status, List<OrderItemResponse> items) {

}


