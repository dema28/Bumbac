package com.bumbac.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление токена")
public class RefreshRequest {

  @NotBlank(message = "Refresh token is required")
  @Schema(description = "Refresh токен для обновления access токена", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
  private String refreshToken;
}
