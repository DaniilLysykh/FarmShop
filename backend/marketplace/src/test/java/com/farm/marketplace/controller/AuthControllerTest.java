package com.farm.marketplace.controller;

import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.LoginRequest;
import com.farm.marketplace.payload.request.SignupRequest;
import com.farm.marketplace.payload.response.JwtResponse;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.repository.UserRepository;
import com.farm.marketplace.security.JwtUtils;
import com.farm.marketplace.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks private AuthController authController;

    @Test
    void register_success() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setPassword("secret1");
        request.setRole("CUSTOMER");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> response = authController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Пользователь успешно зарегистрирован!",
                ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void register_duplicateEmail_returnsBadRequest() {
        SignupRequest request = new SignupRequest();
        request.setEmail("exists@test.com");
        request.setPassword("secret1");
        request.setRole("CUSTOMER");

        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("secret1");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user@test.com", "hash",
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponse body = (JwtResponse) response.getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.getToken());
        assertEquals("user@test.com", body.getEmail());
    }
}
