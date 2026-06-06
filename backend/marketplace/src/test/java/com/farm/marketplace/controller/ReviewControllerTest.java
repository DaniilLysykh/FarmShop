package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.ReviewRequest;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.payload.response.ReviewResponse;
import com.farm.marketplace.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock private ReviewService reviewService;
    @InjectMocks private ReviewController reviewController;

    @Test
    void getProductReviews_returnsList() {
        when(reviewService.getProductReviews(10L, null)).thenReturn(List.of(
                ReviewResponse.builder().id(1L).productId(10L).rating(5).comment("Ok")
                        .createdAt(LocalDateTime.now()).ownReview(false).build()
        ));

        var response = reviewController.getProductReviews(10L, null);

        assertEquals(5, response.getBody().get(0).getRating());
    }

    @Test
    void deleteReview_returnsMessage() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("buyer@test.com");

        var response = reviewController.deleteReview(1L, auth);

        verify(reviewService).deleteReview(1L, "buyer@test.com");
        assertEquals("Отзыв удалён", ((MessageResponse) response.getBody()).getMessage());
    }
}
