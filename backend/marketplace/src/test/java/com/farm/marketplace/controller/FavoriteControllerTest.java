package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.FavoriteRequest;
import com.farm.marketplace.payload.response.FavoriteItemResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock private FavoriteService favoriteService;
    @InjectMocks private FavoriteController favoriteController;

    @Test
    void addToFavorites_delegatesToService() {
        FavoriteRequest request = new FavoriteRequest();
        request.setProductId(10L);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");
        when(favoriteService.addToFavorites(request, "buyer@test.com")).thenReturn(
                FavoriteItemResponse.builder().id(1L).productId(10L).productName("Honey")
                        .price(BigDecimal.TEN).build()
        );

        var response = favoriteController.addToFavorites(request, auth);

        assertEquals(10L, response.getBody().getProductId());
    }

    @Test
    void removeFromFavorites_returnsMessage() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");

        var response = favoriteController.removeFromFavorites(1L, auth);

        verify(favoriteService).removeFromFavorites(1L, "buyer@test.com");
        assertEquals("Товар удален из избранного", ((MessageResponse) response.getBody()).getMessage());
    }
}
