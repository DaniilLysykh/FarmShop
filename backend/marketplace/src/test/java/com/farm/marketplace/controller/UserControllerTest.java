package com.farm.marketplace.controller;

import com.farm.marketplace.model.Role;
import com.farm.marketplace.model.User;
import com.farm.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserController userController;

    @Test
    void getCurrentUser_returnsProfile() {
        User user = User.builder().id(1L).email("user@test.com").passwordHash("hash")
                .roles(Set.of(Role.CUSTOMER)).createdAt(LocalDateTime.now()).build();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        var response = userController.getCurrentUser(auth);

        assertEquals("user@test.com", response.getBody().getEmail());
        assertTrue(response.getBody().getRoles().contains(Role.CUSTOMER));
    }
}
