package com.farm.marketplace.controller;

import com.farm.marketplace.model.OrderStatus;
import com.farm.marketplace.payload.request.OrderRequest;
import com.farm.marketplace.payload.response.OrderResponse;
import com.farm.marketplace.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock private OrderService orderService;
    @InjectMocks private OrderController orderController;

    @Test
    void createOrder_delegatesToService() {
        OrderRequest request = new OrderRequest();
        request.setAddress("Street 1");
        request.setPhone("+79990001122");

        OrderResponse orderResponse = OrderResponse.builder()
                .id(1L).status(OrderStatus.PENDING).address("Street 1")
                .totalPrice(BigDecimal.TEN).createdAt(LocalDateTime.now())
                .items(List.of()).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");
        when(orderService.createOrder(request, "buyer@test.com")).thenReturn(orderResponse);

        ResponseEntity<OrderResponse> response = orderController.createOrder(request, auth);

        assertEquals(OrderStatus.PENDING, response.getBody().getStatus());
        verify(orderService).createOrder(request, "buyer@test.com");
    }

    @Test
    void confirmReceipt_delegatesToService() {
        OrderResponse completed = OrderResponse.builder()
                .id(1L).status(OrderStatus.COMPLETED).address("Street 1")
                .totalPrice(BigDecimal.TEN).createdAt(LocalDateTime.now())
                .items(List.of()).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");
        when(orderService.confirmReceipt(1L, "buyer@test.com")).thenReturn(completed);

        ResponseEntity<OrderResponse> response = orderController.confirmReceipt(1L, auth);

        assertEquals(OrderStatus.COMPLETED, response.getBody().getStatus());
    }
}
