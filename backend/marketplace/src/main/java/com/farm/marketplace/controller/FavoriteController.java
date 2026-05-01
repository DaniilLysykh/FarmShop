package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.FavoriteRequest;
import com.farm.marketplace.payload.response.FavoriteItemResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteItemResponse>> getFavorites(Authentication authentication) {
        return ResponseEntity.ok(favoriteService.getFavorites(authentication.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<FavoriteItemResponse> addToFavorites(@Valid @RequestBody FavoriteRequest request,
                                                               Authentication authentication) {
        return ResponseEntity.ok(favoriteService.addToFavorites(request, authentication.getName()));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<MessageResponse> removeFromFavorites(@PathVariable Long id,
                                                               Authentication authentication) {
        favoriteService.removeFromFavorites(id, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Товар удален из избранного"));
    }
}