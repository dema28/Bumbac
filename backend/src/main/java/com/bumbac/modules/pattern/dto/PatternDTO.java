package com.bumbac.modules.pattern.dto;

import com.bumbac.modules.pattern.entity.Difficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "DTO схемы вязания")
public class PatternDTO {

  @Schema(description = "Уникальный код схемы", example = "SCARF_001")
  private String code;

  @Schema(description = "Название схемы на указанном языке", example = "Уютный шарф")
  private String name;

  @Schema(description = "Описание схемы на указанном языке", example = "Простая схема вязания теплого шарфа для начинающих")
  private String description;

  @Schema(description = "URL к PDF файлу схемы", example = "https://example.com/patterns/scarf_001.pdf")
  private String pdfUrl;

  @Schema(description = "Уровень сложности схемы", example = "INTERMEDIATE")
  private Difficulty difficulty;

  @Schema(description = "Название связанной пряжи", example = "Мягкая шерсть")
  private String yarnName;

  @Schema(description = "ID связанной пряжи", example = "1")
  private Long yarnId;

  @Schema(description = "Дата создания схемы", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Дата последнего обновления схемы", example = "2024-01-20T14:45:00")
  private LocalDateTime updatedAt;
}
