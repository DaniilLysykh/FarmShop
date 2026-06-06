package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.*;
import com.farm.marketplace.payload.request.CartRequest;
import com.farm.marketplace.repository.CartItemRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CartService cartService;

    @Test
    void addToCart_createsNewItem() {
        User user = user(1L, "buyer@test.com");
        Product product = product(10L, "Milk", 5, user);
        CartRequest request = new CartRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> {
            CartItem item = inv.getArgument(0);
            item.setId(100L);
            return item;
        });

        var response = cartService.addToCart(request, "buyer@test.com");

        assertEquals(2, response.getQuantity());
        assertEquals("Milk", response.getProductName());
    }

    @Test
    void addToCart_notEnoughStock_throwsBadRequest() {
        User user = user(1L, "buyer@test.com");
        Product product = product(10L, "Milk", 1, user);
        CartRequest request = new CartRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        AppException ex = assertThrows(AppException.class,
                () -> cartService.addToCart(request, "buyer@test.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void removeFromCart_otherUsersCart_throwsForbidden() {
        User owner = user(1L, "owner@test.com");
        User other = user(2L, "other@test.com");
        Product product = product(10L, "Milk", 5, owner);
        CartItem item = CartItem.builder().id(5L).user(owner).product(product).quantity(1).build();

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(cartItemRepository.findById(5L)).thenReturn(Optional.of(item));

        AppException ex = assertThrows(AppException.class,
                () -> cartService.removeFromCart(5L, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    private User user(Long id, String email) {
        return User.builder().id(id).email(email).passwordHash("hash")
                .createdAt(LocalDateTime.now()).build();
    }

    private Product product(Long id, String name, int stock, User farmer) {
        return Product.builder().id(id).name(name).price(BigDecimal.TEN).unit("kg")
                .stock(stock).category(Category.MILK).farmer(farmer).build();
    }
}
