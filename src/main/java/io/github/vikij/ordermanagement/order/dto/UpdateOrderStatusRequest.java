package io.github.vikij.ordermanagement.order.dto;

import io.github.vikij.ordermanagement.order.entity.OrderStatus;

public class UpdateOrderStatusRequest {

    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }
}
