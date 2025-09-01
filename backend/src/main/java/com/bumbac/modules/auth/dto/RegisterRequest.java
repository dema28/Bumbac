package com.bumbac.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "Запрос на регистрацию пользователя")
public class RegisterRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Schema(description = "Email пользователя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
  @Schema(description = "Пароль пользователя", example = "SecurePass123!", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 30, message = "First name must be 2–30 characters")
  @Pattern(regexp = "^[a-zA-Z\\-\\s]+$", message = "First name must contain only letters, dashes, and spaces")
  @Schema(description = "Имя пользователя", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 2, max = 30, message = "Last name must be 2–30 characters")
  @Pattern(regexp = "^[a-zA-Z\\-\\s]+$", message = "Last name must contain only letters, dashes, and spaces")
  @Schema(description = "Фамилия пользователя", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  private String lastName;

  @NotBlank(message = "Phone is required")
  @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number must be valid and contain 8–15 digits")
  @Schema(description = "Номер телефона", example = "+37360123456", requiredMode = Schema.RequiredMode.REQUIRED)
  private String phone;
}
