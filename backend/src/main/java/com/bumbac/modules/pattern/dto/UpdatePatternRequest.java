package com.bumbac.modules.pattern.dto;

import com.bumbac.modules.pattern.entity.Difficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на обновление схемы вязания")
public class UpdatePatternRequest {

  @Schema(description = "ID связанной пряжи", example = "1")
  private Long yarnId;

  @Schema(description = "URL к PDF файлу схемы", example = "https://example.com/patterns/scarf_001.pdf")
  private String pdfUrl;

  @Schema(description = "Уровень сложности схемы", example = "INTERMEDIATE", allowableValues = { "BEGINNER",
      "INTERMEDIATE", "ADVANCED" })
  private Difficulty difficulty;

  @Schema(description = "Список переводов схемы")
  private List<PatternTranslationUpdateRequest> translations;

  @Data
  @Schema(description = "Обновление перевода схемы на конкретный язык")
  public static class PatternTranslationUpdateRequest {

    @Pattern(regexp = "^[a-z]{2}$", message = "Язык должен быть в формате ISO 639-1 (например: en, ru, md)")
    @Schema(description = "Код языка перевода", example = "en", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locale;

    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    @Schema(description = "Название схемы на указанном языке", example = "Уютный шарф")
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(description = "Описание схемы на указанном языке", example = "Простая схема вязания теплого шарфа для начинающих")
    private String description;
  }
}
