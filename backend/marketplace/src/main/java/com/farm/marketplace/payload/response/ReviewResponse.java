package com.farm.marketplace.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String userEmail;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean ownReview;
}
