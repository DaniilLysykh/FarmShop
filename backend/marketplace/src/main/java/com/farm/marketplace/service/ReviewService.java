package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.NotificationType;
import com.farm.marketplace.model.Product;
import com.farm.marketplace.model.Review;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.ReviewRequest;
import com.farm.marketplace.payload.response.ReviewResponse;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.ReviewRepository;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private ReviewResponse mapToResponse(Review review, Long currentUserId) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .userEmail(maskEmail(review.getUser().getEmail()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .ownReview(currentUserId != null && review.getUser().getId().equals(currentUserId))
                .build();
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    public List<ReviewResponse> getProductReviews(Long productId, String email) {
        if (!productRepository.existsById(productId)) {
            throw new AppException("Товар не найден", HttpStatus.NOT_FOUND);
        }
        Long currentUserId = null;
        if (email != null) {
            currentUserId = userRepository.findByEmail(email).map(User::getId).orElse(null);
        }
        Long finalCurrentUserId = currentUserId;
        return reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(r -> mapToResponse(r, finalCurrentUserId))
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews(String email) {
        User user = getUserByEmail(email);
        return reviewRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(r -> mapToResponse(r, user.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String email) {
        User user = getUserByEmail(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new AppException("Вы уже оставляли отзыв на этот товар", HttpStatus.BAD_REQUEST);
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        User farmer = product.getFarmer();
        notificationService.createNotification(
                farmer,
                NotificationType.NEW_REVIEW,
                "Новый отзыв",
                "Покупатель оставил отзыв (" + request.getRating() + "★) на товар «" + product.getName() + "»",
                product.getId()
        );

        return mapToResponse(saved, user.getId());
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, String email) {
        User user = getUserByEmail(email);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException("Отзыв не найден", HttpStatus.NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа", HttpStatus.FORBIDDEN);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return mapToResponse(reviewRepository.save(review), user.getId());
    }

    @Transactional
    public void deleteReview(Long reviewId, String email) {
        User user = getUserByEmail(email);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException("Отзыв не найден", HttpStatus.NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа", HttpStatus.FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

    public Double getAverageRating(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }

    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }
}
