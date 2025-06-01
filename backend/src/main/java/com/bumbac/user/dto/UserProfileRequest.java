package com.bumbac.user.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
}
