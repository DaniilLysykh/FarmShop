package com.farm.marketplace.controller;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.response.UserProfileResponse;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        // authentication содержит данные из нашего токена
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }
}