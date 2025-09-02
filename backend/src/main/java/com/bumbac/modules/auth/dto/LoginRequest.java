package com.bumbac.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Запрос на авторизацию пользователя")
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Schema(description = "Email пользователя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  @Schema(description = "Пароль пользователя", example = "SecurePass123!", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
