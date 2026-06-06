package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.*;
import com.farm.marketplace.payload.request.ReviewRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks private ReviewService reviewService;

    @Test
    void createReview_success_notifiesFarmer() {
        User buyer = user(1L, "buyer@test.com");
        User farmer = user(2L, "farmer@test.com");
        Product product = product(10L, "Milk", farmer);
        ReviewRequest request = new ReviewRequest();
        request.setProductId(10L);
        request.setRating(5);
        request.setComment("Great");

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(buyer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review review = inv.getArgument(0);
            review.setId(100L);
            review.setCreatedAt(LocalDateTime.now());
            return review;
        });

        var response = reviewService.createReview(request, "buyer@test.com");

        assertEquals(5, response.getRating());
        assertTrue(response.isOwnReview());
        verify(notificationService).createNotification(
                eq(farmer), eq(NotificationType.NEW_REVIEW), eq("Новый отзыв"), anyString(), eq(10L));
    }

    @Test
    void createReview_duplicate_throwsBadRequest() {
        User buyer = user(1L, "buyer@test.com");
        Product product = product(10L, "Milk", user(2L, "farmer@test.com"));
        ReviewRequest request = new ReviewRequest();
        request.setProductId(10L);
        request.setRating(4);

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(buyer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(true);

        AppException ex = assertThrows(AppException.class,
                () -> reviewService.createReview(request, "buyer@test.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void getProductReviews_masksEmail() {
        User author = user(3L, "john.doe@mail.ru");
        Product product = product(10L, "Milk", user(2L, "farmer@test.com"));
        Review review = Review.builder().id(1L).user(author).product(product)
                .rating(5).comment("Ok").createdAt(LocalDateTime.now()).build();

        when(productRepository.existsById(10L)).thenReturn(true);
        when(reviewRepository.findAllByProductIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(review));

        var reviews = reviewService.getProductReviews(10L, null);

        assertEquals("j***@mail.ru", reviews.get(0).getUserEmail());
    }

    private User user(Long id, String email) {
        return User.builder().id(id).email(email).passwordHash("hash")
                .createdAt(LocalDateTime.now()).build();
    }

    private Product product(Long id, String name, User farmer) {
        return Product.builder().id(id).name(name).price(BigDecimal.TEN).unit("l")
                .stock(10).category(Category.MILK).farmer(farmer).build();
    }
}
