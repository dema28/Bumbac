package com.bumbac.modules.media.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Типы сущностей, к которым могут прикрепляться медиафайлы")
public enum MediaEntityType {

  @Schema(description = "Пряжа")
  YARN,

  @Schema(description = "Цвет пряжи")
  COLOR,

  @Schema(description = "Схема вязания")
  PATTERN,

  @Schema(description = "Бренд")
  BRAND,

  @Schema(description = "Категория")
  CATEGORY,

  @Schema(description = "Коллекция")
  COLLECTION
}
