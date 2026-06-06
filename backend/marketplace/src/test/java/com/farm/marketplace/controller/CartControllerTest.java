package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.CartRequest;
import com.farm.marketplace.payload.response.CartItemResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock private CartService cartService;
    @InjectMocks private CartController cartController;

    @Test
    void getCart_returnsItems() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");
        when(cartService.getCart("buyer@test.com")).thenReturn(List.of(
                CartItemResponse.builder().id(1L).productId(10L).productName("Milk")
                        .price(BigDecimal.TEN).quantity(2).build()
        ));

        var response = cartController.getCart(auth);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void removeFromCart_returnsMessage() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");

        var response = cartController.removeFromCart(5L, auth);

        verify(cartService).removeFromCart(5L, "buyer@test.com");
        assertEquals("Товар удален из корзины", ((MessageResponse) response.getBody()).getMessage());
    }
}
