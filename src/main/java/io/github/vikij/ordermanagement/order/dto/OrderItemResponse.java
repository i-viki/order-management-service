package io.github.vikij.ordermanagement.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(String productCode, Integer quantity, BigDecimal unitPrice) {

}
