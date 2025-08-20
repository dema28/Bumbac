package com.bumbac.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePasswordRequest(
        @Schema(example = "OldStrong1")
        @NotBlank(message = "Old password is required")
        String oldPassword,

        @Schema(example = "NewStrong1", minLength = 8, maxLength = 64)
        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String newPassword
) {}
