package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.CartRequest;
import com.farm.marketplace.payload.response.CartItemResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody CartRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(cartService.addToCart(request, authentication.getName()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable Long id,
                                                           @RequestParam Integer quantity,
                                                           Authentication authentication) {
        return ResponseEntity.ok(cartService.updateQuantity(id, quantity, authentication.getName()));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<MessageResponse> removeFromCart(@PathVariable Long id,
                                                          Authentication authentication) {
        cartService.removeFromCart(id, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Товар удален из корзины"));
    }
}