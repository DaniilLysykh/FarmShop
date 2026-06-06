package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.payload.request.FavoriteRequest;
import com.farm.marketplace.payload.response.FavoriteItemResponse;
import com.farm.marketplace.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(controllers = FavoriteController.class)
@ControllerTestSetup
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavoriteService favoriteService;

    private final FavoriteItemResponse sampleItem = FavoriteItemResponse.builder()
            .id(1L)
            .productId(10L)
            .productName("Honey")
            .price(new BigDecimal("250.00"))
            .build();

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void getFavorites_returnsItems() throws Exception {
        when(favoriteService.getFavorites("buyer@test.com")).thenReturn(List.of(sampleItem));

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Honey"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void addToFavorites_success() throws Exception {
        FavoriteRequest request = new FavoriteRequest();
        request.setProductId(10L);

        when(favoriteService.addToFavorites(any(FavoriteRequest.class), eq("buyer@test.com")))
                .thenReturn(sampleItem);

        mockMvc.perform(post("/api/favorites/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(10));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void removeFromFavorites_success() throws Exception {
        mockMvc.perform(delete("/api/favorites/remove/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Товар удален из избранного"));

        verify(favoriteService).removeFromFavorites(1L, "buyer@test.com");
    }

    @Test
    void getFavorites_withoutAuth_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isForbidden());
    }
}
