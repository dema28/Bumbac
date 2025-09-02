package com.bumbac.modules.newsletter.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Запрос на подписку на рассылку")
public class NewsletterRequest {

  @NotBlank(message = "Email обязателен")
  @Email(message = "Неверный формат email")
  @Schema(description = "Email адрес для подписки на рассылку", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  // Убираем дублирующие геттер и сеттер - Lombok @Data их уже создает
}