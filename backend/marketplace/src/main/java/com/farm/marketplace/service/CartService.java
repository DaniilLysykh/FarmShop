package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.CartItem;
import com.farm.marketplace.model.Product;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.CartRequest;
import com.farm.marketplace.payload.response.CartItemResponse;
import com.farm.marketplace.repository.CartItemRepository;
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
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private CartItemResponse mapToResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .imageUrl(item.getProduct().getImageUrl())
                .build();
    }

    public List<CartItemResponse> getCart(String email) {
        User user = getUserByEmail(email);
        return cartItemRepository.findAllByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CartItemResponse addToCart(CartRequest request, String email) {
        User user = getUserByEmail(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        if (product.getStock() < request.getQuantity()) {
            throw new AppException("Недостаточно товара на складе", HttpStatus.BAD_REQUEST);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());

        CartItem savedItem;
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new AppException("Недостаточно товара на складе для добавления такого количества", HttpStatus.BAD_REQUEST);
            }
            item.setQuantity(newQuantity);
            savedItem = cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            savedItem = cartItemRepository.save(newItem);
        }

        return mapToResponse(savedItem);
    }

    public CartItemResponse updateQuantity(Long cartItemId, Integer quantity, String email) {
        User user = getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException("Элемент корзины не найден", HttpStatus.NOT_FOUND));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа к чужой корзине", HttpStatus.FORBIDDEN);
        }

        if (item.getProduct().getStock() < quantity) {
            throw new AppException("Недостаточно товара на складе", HttpStatus.BAD_REQUEST);
        }

        item.setQuantity(quantity);
        return mapToResponse(cartItemRepository.save(item));
    }

    public void removeFromCart(Long cartItemId, String email) {
        User user = getUserByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException("Элемент корзины не найден", HttpStatus.NOT_FOUND));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа к чужой корзине", HttpStatus.FORBIDDEN);
        }

        cartItemRepository.delete(item);
    }
}