package io.github.vikij.ordermanagement.order.mapper;

import io.github.vikij.ordermanagement.order.dto.OrderItemResponse;
import io.github.vikij.ordermanagement.order.dto.OrderResponse;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem item);

    List<OrderResponse> toResponseList(List<Order> orders);
}
