package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.Category;
import com.farm.marketplace.model.Product;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.ProductRequest;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.ReviewRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private ProductService productService;

    @Test
    void createProduct_success() {
        User farmer = user(2L, "farmer@test.com");
        ProductRequest request = new ProductRequest();
        request.setName("Milk");
        request.setPrice(new BigDecimal("120"));
        request.setUnit("l");
        request.setStock(10);
        request.setCategory("MILK");

        when(userRepository.findByEmail("farmer@test.com")).thenReturn(Optional.of(farmer));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(null);
        when(reviewRepository.countByProductId(1L)).thenReturn(0L);

        var response = productService.createProduct(request, "farmer@test.com");

        assertEquals("Milk", response.getName());
        assertEquals(Category.MILK, response.getCategory());
    }

    @Test
    void updateProduct_notOwner_throwsForbidden() {
        User farmer = user(2L, "farmer@test.com");
        User otherFarmer = user(3L, "other@test.com");
        Product product = Product.builder().id(1L).name("Milk").price(BigDecimal.TEN).unit("l")
                .stock(5).category(Category.MILK).farmer(otherFarmer).build();
        ProductRequest request = new ProductRequest();
        request.setName("Milk");
        request.setPrice(BigDecimal.TEN);
        request.setUnit("l");
        request.setStock(5);
        request.setCategory("MILK");

        when(userRepository.findByEmail("farmer@test.com")).thenReturn(Optional.of(farmer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        AppException ex = assertThrows(AppException.class,
                () -> productService.updateProduct(1L, request, "farmer@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void deleteProduct_success() {
        User farmer = user(2L, "farmer@test.com");
        Product product = Product.builder().id(1L).name("Milk").price(BigDecimal.TEN).unit("l")
                .stock(5).category(Category.MILK).farmer(farmer).build();

        when(userRepository.findByEmail("farmer@test.com")).thenReturn(Optional.of(farmer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L, "farmer@test.com");

        verify(productRepository).delete(product);
    }

    private User user(Long id, String email) {
        return User.builder().id(id).email(email).passwordHash("hash")
                .createdAt(LocalDateTime.now()).build();
    }
}
