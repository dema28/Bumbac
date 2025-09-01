package com.bumbac.modules.pattern.controller;

import com.bumbac.modules.pattern.dto.PatternDTO;
import com.bumbac.modules.pattern.dto.CreatePatternRequest;
import com.bumbac.modules.pattern.dto.UpdatePatternRequest;
import com.bumbac.modules.pattern.service.PatternService;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Patterns", description = "API для управления схемами вязания")
@Slf4j
public class PatternController {

  private final PatternService patternService;

  @GetMapping
  @Operation(summary = "Получить все схемы вязания", description = "Возвращает список всех доступных схем вязания на заданном языке")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список схем получен", content = @Content(schema = @Schema(implementation = PatternDTO.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный параметр языка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<PatternDTO>> getAll(
      @RequestParam(defaultValue = "en") @Pattern(regexp = "^[a-z]{2}$", message = "Язык должен быть в формате ISO 639-1 (например: en, ru, md)") String lang) {

    log.debug("Получение всех схем вязания для языка: {}", lang);
    List<PatternDTO> patterns = patternService.getAll(lang);
    return ResponseEntity.ok(patterns);
  }

  @GetMapping("/{code}")
  @Operation(summary = "Получить схему по коду", description = "Возвращает схему вязания по коду и языку")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Схема найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatternDTO.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный параметр языка", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Схема не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PatternDTO> getByCode(
      @PathVariable @NotBlank(message = "Код схемы обязателен") String code,
      @RequestParam(defaultValue = "en") @Pattern(regexp = "^[a-z]{2}$", message = "Язык должен быть в формате ISO 639-1 (например: en, ru, md)") String lang) {

    log.debug("Получение схемы вязания по коду: {} для языка: {}", code, lang);
    return patternService.getByCode(code, lang)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(summary = "Создать новую схему вязания", description = "Создаёт новую схему вязания в системе")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Схема успешно создана", content = @Content(schema = @Schema(implementation = PatternDTO.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации данных", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PatternDTO> createPattern(@Valid @RequestBody CreatePatternRequest request) {
    log.info("Создание новой схемы вязания: {}", request.getCode());
    PatternDTO createdPattern = patternService.createPattern(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPattern);
  }

  @PutMapping("/{code}")
  @Operation(summary = "Обновить схему вязания", description = "Обновляет существующую схему вязания")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Схема успешно обновлена", content = @Content(schema = @Schema(implementation = PatternDTO.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации данных", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Схема не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<PatternDTO> updatePattern(
      @PathVariable @NotBlank(message = "Код схемы обязателен") String code,
      @Valid @RequestBody UpdatePatternRequest request) {

    log.info("Обновление схемы вязания: {}", code);
    PatternDTO updatedPattern = patternService.updatePattern(code, request);
    return ResponseEntity.ok(updatedPattern);
  }

  @DeleteMapping("/{code}")
  @Operation(summary = "Удалить схему вязания", description = "Удаляет схему вязания по коду")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Схема успешно удалена"),
      @ApiResponse(responseCode = "404", description = "Схема не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> deletePattern(
      @PathVariable @NotBlank(message = "Код схемы обязателен") String code) {

    log.info("Удаление схемы вязания: {}", code);
    patternService.deletePattern(code);
    return ResponseEntity.noContent().build();
  }
}
