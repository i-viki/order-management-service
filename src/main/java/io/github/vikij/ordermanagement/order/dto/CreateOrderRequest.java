package io.github.vikij.ordermanagement.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {

    private DeliveryAddressRequest deliveryAddress;

    @NotEmpty
    private List<OrderItemRequest> items;
    public DeliveryAddressRequest getDeliveryAddress() {
        return deliveryAddress;
    }

    public static class DeliveryAddressRequest {

        @NotBlank
        private String addressLine;

        private String city;
        private String country;
        private String postalCode;

        public String getAddressLine() { return addressLine; }
        public String getCity() { return city; }
        public String getCountry() { return country; }
        public String getPostalCode() { return postalCode; }
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public static class OrderItemRequest {

        @NotNull
        private String productCode;

        @NotNull
        private Integer quantity;

        @NotNull
        private BigDecimal unitPrice;

        public String getProductCode() {
            return productCode;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
