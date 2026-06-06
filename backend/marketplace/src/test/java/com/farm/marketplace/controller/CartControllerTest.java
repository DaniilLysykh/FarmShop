package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.payload.request.CartRequest;
import com.farm.marketplace.payload.response.CartItemResponse;
import com.farm.marketplace.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTestSetup
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private final CartItemResponse sampleItem = CartItemResponse.builder()
            .id(1L)
            .productId(10L)
            .productName("Milk")
            .price(new BigDecimal("100.00"))
            .quantity(2)
            .build();

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void getCart_returnsItems() throws Exception {
        when(cartService.getCart("buyer@test.com")).thenReturn(List.of(sampleItem));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Milk"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void addToCart_success() throws Exception {
        CartRequest request = new CartRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(cartService.addToCart(any(CartRequest.class), eq("buyer@test.com"))).thenReturn(sampleItem);

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void updateQuantity_success() throws Exception {
        when(cartService.updateQuantity(1L, 5, "buyer@test.com")).thenReturn(sampleItem);

        mockMvc.perform(put("/api/cart/update/1").param("quantity", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void removeFromCart_success() throws Exception {
        mockMvc.perform(delete("/api/cart/remove/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Товар удален из корзины"));

        verify(cartService).removeFromCart(1L, "buyer@test.com");
    }

    @Test
    void getCart_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isForbidden());
    }
}
