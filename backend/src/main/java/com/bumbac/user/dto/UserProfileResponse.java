package com.bumbac.user.dto;

import com.bumbac.auth.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String phone;
}
