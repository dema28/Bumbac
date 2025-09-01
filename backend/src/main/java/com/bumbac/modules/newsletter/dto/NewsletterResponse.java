package com.bumbac.modules.newsletter.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Ответ с информацией о подписчике на рассылку")
public class NewsletterResponse {

  @Schema(description = "Уникальный идентификатор подписчика", example = "1")
  private Long id;

  @Schema(description = "Email адрес подписчика", example = "user@example.com")
  private String email;

  @Schema(description = "Статус подтверждения подписки", example = "true")
  private Boolean confirmed;

  @Schema(description = "Статус отписки от рассылки", example = "false")
  private Boolean unsubscribed;

  @Schema(description = "Дата и время подписки", example = "2024-01-15T10:30:00")
  private LocalDateTime subscribedAt;
}
