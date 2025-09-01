package com.bumbac.modules.newsletter.entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_subscribers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность подписчика на рассылку")
public class NewsletterSubscriber {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор")
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  @Schema(description = "Email адрес подписчика", example = "user@example.com")
  private String email;

  @Builder.Default
  @Column(nullable = false)
  @Schema(description = "Статус подтверждения подписки", example = "false")
  private Boolean confirmed = false;

  @Column(length = 255)
  @Schema(description = "Токен для подтверждения подписки")
  private String confirmationToken;

  @Builder.Default
  @Column(nullable = false)
  @Schema(description = "Статус отписки от рассылки", example = "false")
  private Boolean unsubscribed = false;

  @Builder.Default
  @Column(nullable = false)
  @Schema(description = "Дата и время подписки")
  private LocalDateTime subscribedAt = LocalDateTime.now();
}
