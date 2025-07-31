package com.bumbac.modules.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be 2–30 characters")
    @Pattern(regexp = "^[a-zA-Z\\-\\s]+$", message = "First name must contain only letters, dashes, and spaces")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name must be 2–30 characters")
    @Pattern(regexp = "^[a-zA-Z\\-\\s]+$", message = "Last name must contain only letters, dashes, and spaces")
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number must be valid and contain 8–15 digits")
    private String phone;
}
