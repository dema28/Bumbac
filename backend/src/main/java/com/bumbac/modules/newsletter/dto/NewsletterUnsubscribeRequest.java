package com.bumbac.modules.newsletter.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Запрос на отписку от рассылки")
public class NewsletterUnsubscribeRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Неверный формат email")
  @Schema(description = "Email адрес для отписки от рассылки", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;
}