package com.farm.marketplace.payload.response;

import com.farm.marketplace.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private Set<Role> roles;
    private LocalDateTime createdAt;
}