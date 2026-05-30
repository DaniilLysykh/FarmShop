package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.ReviewRequest;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.payload.response.ReviewResponse;
import com.farm.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId,
                                                                  Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getProductReviews(productId, email));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        return ResponseEntity.ok(reviewService.getMyReviews(authentication.getName()));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(reviewService.createReview(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id,
                                                       @Valid @RequestBody ReviewRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(reviewService.updateReview(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long id,
                                                        Authentication authentication) {
        reviewService.deleteReview(id, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Отзыв удалён"));
    }
}
