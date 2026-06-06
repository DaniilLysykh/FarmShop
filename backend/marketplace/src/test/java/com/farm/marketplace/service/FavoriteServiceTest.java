package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.*;
import com.farm.marketplace.payload.request.FavoriteRequest;
import com.farm.marketplace.repository.FavoriteItemRepository;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock private FavoriteItemRepository favoriteItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private FavoriteService favoriteService;

    @Test
    void addToFavorites_success() {
        User user = user(1L, "buyer@test.com");
        Product product = product(10L, "Honey", user);
        FavoriteRequest request = new FavoriteRequest();
        request.setProductId(10L);

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(favoriteItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(favoriteItemRepository.save(org.mockito.ArgumentMatchers.any(FavoriteItem.class)))
                .thenAnswer(inv -> {
                    FavoriteItem item = inv.getArgument(0);
                    item.setId(50L);
                    return item;
                });

        var response = favoriteService.addToFavorites(request, "buyer@test.com");

        assertEquals(10L, response.getProductId());
        assertEquals("Honey", response.getProductName());
    }

    @Test
    void addToFavorites_alreadyExists_throwsBadRequest() {
        User user = user(1L, "buyer@test.com");
        Product product = product(10L, "Honey", user);
        FavoriteRequest request = new FavoriteRequest();
        request.setProductId(10L);

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(favoriteItemRepository.findByUserIdAndProductId(1L, 10L))
                .thenReturn(Optional.of(FavoriteItem.builder().id(1L).user(user).product(product).build()));

        AppException ex = assertThrows(AppException.class,
                () -> favoriteService.addToFavorites(request, "buyer@test.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    private User user(Long id, String email) {
        return User.builder().id(id).email(email).passwordHash("hash")
                .createdAt(LocalDateTime.now()).build();
    }

    private Product product(Long id, String name, User farmer) {
        return Product.builder().id(id).name(name).price(BigDecimal.TEN).unit("kg")
                .stock(10).category(Category.HONEY).farmer(farmer).build();
    }
}
