package com.bumbac.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Избранная пряжа пользователя")
public class FavoriteDTO {

  @NotNull(message = "ID пряжи обязателен")
  @Positive(message = "ID пряжи должен быть положительным числом")
  @Schema(description = "ID пряжи", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long yarnId;

  @Schema(description = "Название пряжи", example = "Super Soft Cotton")
  private String yarnName;

  @Schema(description = "Название цвета", example = "Красный")
  private String colorName;

  @Schema(description = "Цена пряжи в MDL", example = "49.95")
  private BigDecimal priceMDL;

  @Schema(description = "Цена пряжи в USD", example = "2.95")
  private BigDecimal priceUSD;

  @Schema(description = "Изображение пряжи", example = "https://example.com/image.jpg")
  private String imageUrl;

  @Schema(description = "Дата добавления в избранное", example = "2024-01-15T10:30:00")
  private LocalDateTime addedAt;

  @Schema(description = "Заметки пользователя", example = "Хочу связать свитер")
  private String notes;
}
