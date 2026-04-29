package io.github.vikij.ordermanagement.order.service;

import io.github.vikij.ordermanagement.order.dto.CreateOrderRequest;
import io.github.vikij.ordermanagement.order.entity.Order;
import io.github.vikij.ordermanagement.order.entity.OrderStatus;
import io.github.vikij.ordermanagement.order.repository.OrderRepository;
import io.github.vikij.ordermanagement.user.entity.AppUser;
import io.github.vikij.ordermanagement.user.entity.Role;
import io.github.vikij.ordermanagement.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private AppUser mockUser;
    private Authentication mockAuth;

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .role(Role.USER)
                .build();

        mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser");
    }

    @Test
    void createOrder_ShouldCalculateTotalsAndSave() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .deliveryAddress(CreateOrderRequest.DeliveryAddressRequest.builder()
                        .addressLine("123 Street")
                        .city("Chennai")
                        .country("India")
                        .postalCode("600001")
                        .build())
                .items(List.of(
                        CreateOrderRequest.OrderItemRequest.builder()
                                .productCode("P1")
                                .quantity(2)
                                .unitPrice(new BigDecimal("100.00"))
                                .build()
                ))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Order savedOrder = orderService.createOrder(request, mockAuth);

        // Assert
        assertNotNull(savedOrder);
        assertEquals(new BigDecimal("200.00"), savedOrder.getSubtotal());
        assertEquals(new BigDecimal("36.00"), savedOrder.getTaxAmount()); // 18% of 200
        assertEquals(new BigDecimal("236.00"), savedOrder.getTotalAmount());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateStatus_ShouldUpdateAndSave() {
        // Arrange
        Order order = Order.builder()
                .orderNumber("ORD-123")
                .status(OrderStatus.CREATED)
                .build();

        when(orderRepository.findByOrderNumber("ORD-123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Order updated = orderService.updateStatus("ORD-123", OrderStatus.PROCESSING);

        // Assert
        assertEquals(OrderStatus.PROCESSING, updated.getStatus());
        verify(orderRepository).save(order);
    }
}
