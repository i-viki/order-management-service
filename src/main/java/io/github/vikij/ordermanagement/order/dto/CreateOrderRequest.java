package io.github.vikij.ordermanagement.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    @Valid
    private DeliveryAddressRequest deliveryAddress;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryAddressRequest {
        @NotBlank
        private String addressLine;
        @NotBlank
        private String city;
        @NotBlank
        private String country;
        @NotBlank
        private String postalCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotBlank
        private String productCode;
        @NotNull
        @Min(1)
        private Integer quantity;
        @NotNull
        @Min(0)
        private BigDecimal unitPrice;
    }
}
