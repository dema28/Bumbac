package com.bumbac.modules.pattern.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Уровень сложности схемы вязания")
public enum Difficulty {

  @Schema(description = "Начинающий уровень")
  BEGINNER,

  @Schema(description = "Средний уровень")
  INTERMEDIATE,

  @Schema(description = "Продвинутый уровень")
  ADVANCED
}
