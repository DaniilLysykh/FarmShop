package com.farm.marketplace.controller;

import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.model.Role;
import com.farm.marketplace.model.User;
import com.farm.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ControllerTestSetup
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user@test.com")
    void getCurrentUser_success() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .passwordHash("hash")
                .roles(Set.of(Role.CUSTOMER))
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));
    }
}
