package com.bumbac.modules.media.dto;

import com.bumbac.modules.media.entity.MediaEntityType;
import com.bumbac.modules.media.entity.MediaVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Запрос на загрузку медиафайла")
public class UploadMediaRequest {

  @NotNull(message = "Тип сущности обязателен")
  @Schema(description = "Тип сущности, к которой прикрепляется медиафайл", example = "YARN", requiredMode = Schema.RequiredMode.REQUIRED)
  private MediaEntityType entityType;

  @NotNull(message = "ID сущности обязателен")
  @Positive(message = "ID сущности должен быть положительным числом")
  @Schema(description = "ID сущности, к которой прикрепляется медиафайл", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long entityId;

  @Schema(description = "Вариант медиафайла (основной, миниатюра и т.п.)", example = "MAIN")
  private MediaVariant variant;

  @Schema(description = "Альтернативный текст для изображения", example = "Изображение мотка пряжи")
  private String altText;

  @Schema(description = "Порядок сортировки медиафайла", example = "1")
  private Integer sortOrder;
}
