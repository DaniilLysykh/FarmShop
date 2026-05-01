package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.FavoriteItem;
import com.farm.marketplace.model.Product;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.FavoriteRequest;
import com.farm.marketplace.payload.response.FavoriteItemResponse;
import com.farm.marketplace.repository.FavoriteItemRepository;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteItemRepository favoriteItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private FavoriteItemResponse mapToResponse(FavoriteItem item) {
        return FavoriteItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .imageUrl(item.getProduct().getImageUrl())
                .build();
    }

    public List<FavoriteItemResponse> getFavorites(String email) {
        User user = getUserByEmail(email);
        return favoriteItemRepository.findAllByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FavoriteItemResponse addToFavorites(FavoriteRequest request, String email) {
        User user = getUserByEmail(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        // Проверяем, нет ли уже товара в избранном
        Optional<FavoriteItem> existingItem = favoriteItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (existingItem.isPresent()) {
            throw new AppException("Товар уже находится в избранном", HttpStatus.BAD_REQUEST);
        }

        FavoriteItem newItem = FavoriteItem.builder()
                .user(user)
                .product(product)
                .build();

        return mapToResponse(favoriteItemRepository.save(newItem));
    }

    public void removeFromFavorites(Long favoriteItemId, String email) {
        User user = getUserByEmail(email);
        FavoriteItem item = favoriteItemRepository.findById(favoriteItemId)
                .orElseThrow(() -> new AppException("Элемент не найден в избранном", HttpStatus.NOT_FOUND));

        // Проверка прав (нельзя удалить чужое избранное)
        if (!item.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа", HttpStatus.FORBIDDEN);
        }

        favoriteItemRepository.delete(item);
    }
}