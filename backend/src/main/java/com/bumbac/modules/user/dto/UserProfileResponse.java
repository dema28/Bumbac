package com.bumbac.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с профилем пользователя")
public class UserProfileResponse {

  @Schema(description = "Уникальный идентификатор пользователя", example = "1")
  private Long id;

  @Schema(description = "Email пользователя", example = "user@example.com")
  private String email;

  @Schema(description = "Список ролей пользователя", example = "[\"USER\", \"ADMIN\"]")
  private List<String> roles;

  @Schema(description = "Имя пользователя", example = "Иван")
  private String firstName;

  @Schema(description = "Фамилия пользователя", example = "Иванов")
  private String lastName;

  @Schema(description = "Номер телефона", example = "+37360123456")
  private String phone;

  @Schema(description = "Дата создания аккаунта", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Дата последнего обновления профиля", example = "2024-01-15T10:30:00")
  private LocalDateTime updatedAt;

  @Schema(description = "Количество избранных товаров", example = "5")
  private Integer favoritesCount;

  @Schema(description = "Количество заказов", example = "3")
  private Integer ordersCount;
}
