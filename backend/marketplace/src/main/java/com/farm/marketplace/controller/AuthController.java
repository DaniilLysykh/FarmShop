package com.farm.marketplace.controller;

import com.farm.marketplace.model.Role;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.LoginRequest;
import com.farm.marketplace.payload.request.SignupRequest;
import com.farm.marketplace.payload.response.JwtResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.repository.UserRepository;
import com.farm.marketplace.security.JwtUtils;
import com.farm.marketplace.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth") // Базовый URL для этого контроллера
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    // Регистрация
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Проверяем, не занят ли email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: Email уже используется!"));
        }

        // Преобразуем строковую роль в наш Enum
        Role userRole;
        try {
            userRole = Role.valueOf(signUpRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Ошибка: Указана неверная роль. Используйте CUSTOMER или FARMER."));
        }

        // Создаем нового пользователя
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .passwordHash(encoder.encode(signUpRequest.getPassword())) // Хэшируем пароль!
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Пользователь успешно зарегистрирован!"));
    }

    // Авторизация (Логин)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Spring Security сам проверяет логин и пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем JWT токен
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Достаем данные пользователя
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Возвращаем токен и данные клиенту
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles));
    }
}