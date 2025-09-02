package com.bumbac.modules.pattern.dto;

import com.bumbac.modules.pattern.entity.Difficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на создание схемы вязания")
public class CreatePatternRequest {

  @NotBlank(message = "Код схемы обязателен")
  @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Код схемы должен содержать только заглавные буквы, цифры, дефисы и подчеркивания")
  @Size(min = 3, max = 20, message = "Код схемы должен быть от 3 до 20 символов")
  @Schema(description = "Уникальный код схемы вязания", example = "SCARF_001", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @NotNull(message = "ID пряжи обязателен")
  @Schema(description = "ID связанной пряжи", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long yarnId;

  @Schema(description = "URL к PDF файлу схемы", example = "https://example.com/patterns/scarf_001.pdf")
  private String pdfUrl;

  @Schema(description = "Уровень сложности схемы", example = "INTERMEDIATE", allowableValues = { "BEGINNER",
      "INTERMEDIATE", "ADVANCED" })
  private Difficulty difficulty;

  @Schema(description = "Список переводов схемы", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<PatternTranslationRequest> translations;

  @Data
  @Schema(description = "Перевод схемы на конкретный язык")
  public static class PatternTranslationRequest {

    @NotBlank(message = "Язык перевода обязателен")
    @Pattern(regexp = "^[a-z]{2}$", message = "Язык должен быть в формате ISO 639-1 (например: en, ru, md)")
    @Schema(description = "Код языка перевода", example = "en", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locale;

    @NotBlank(message = "Название схемы обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    @Schema(description = "Название схемы на указанном языке", example = "Уютный шарф", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    @Schema(description = "Описание схемы на указанном языке", example = "Простая схема вязания теплого шарфа для начинающих")
    private String description;
  }
}
