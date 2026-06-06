package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.payload.request.ReviewRequest;
import com.farm.marketplace.payload.response.ReviewResponse;
import com.farm.marketplace.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@ControllerTestSetup
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private final ReviewResponse sampleReview = ReviewResponse.builder()
            .id(1L)
            .productId(10L)
            .productName("Milk")
            .userId(2L)
            .userEmail("b***@test.com")
            .rating(5)
            .comment("Great")
            .createdAt(LocalDateTime.now())
            .ownReview(true)
            .build();

    @Test
    void getProductReviews_publicAccess() throws Exception {
        when(reviewService.getProductReviews(eq(10L), any())).thenReturn(List.of(sampleReview));

        mockMvc.perform(get("/api/reviews/product/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void getMyReviews_success() throws Exception {
        when(reviewService.getMyReviews("buyer@test.com")).thenReturn(List.of(sampleReview));

        mockMvc.perform(get("/api/reviews/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownReview").value(true));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void createReview_success() throws Exception {
        ReviewRequest request = buildReviewRequest();

        when(reviewService.createReview(any(ReviewRequest.class), eq("buyer@test.com")))
                .thenReturn(sampleReview);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Great"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void updateReview_success() throws Exception {
        ReviewRequest request = buildReviewRequest();

        when(reviewService.updateReview(eq(1L), any(ReviewRequest.class), eq("buyer@test.com")))
                .thenReturn(sampleReview);

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void deleteReview_success() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Отзыв удалён"));

        verify(reviewService).deleteReview(1L, "buyer@test.com");
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void createReview_asFarmer_returnsForbidden() throws Exception {
        ReviewRequest request = buildReviewRequest();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private ReviewRequest buildReviewRequest() {
        ReviewRequest request = new ReviewRequest();
        request.setProductId(10L);
        request.setRating(5);
        request.setComment("Great");
        return request;
    }
}
