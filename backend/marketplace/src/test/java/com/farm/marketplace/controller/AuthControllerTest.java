package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.payload.request.LoginRequest;
import com.farm.marketplace.payload.request.SignupRequest;
import com.farm.marketplace.model.User;
import com.farm.marketplace.repository.UserRepository;
import com.farm.marketplace.security.JwtUtils;
import com.farm.marketplace.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTestSetup
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void register_success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setPassword("secret1");
        request.setRole("CUSTOMER");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно зарегистрирован!"));
    }

    @Test
    void register_duplicateEmail_returnsBadRequest() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("exists@test.com");
        request.setPassword("secret1");
        request.setRole("CUSTOMER");

        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка: Email уже используется!"));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("secret1");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "user@test.com",
                "hash",
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }
}
