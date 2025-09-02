package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.YarnRequest;
import com.bumbac.modules.catalog.dto.YarnResponse;
import com.bumbac.modules.catalog.service.YarnService;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/yarns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Yarn Catalog", description = "API для управления каталогом пряжи")
@Slf4j
public class YarnController {

  private final YarnService yarnService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Создать пряжу", description = "Добавляет новую пряжу в каталог")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа успешно создана", content = @Content(schema = @Schema(implementation = YarnResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Связанная сущность не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> create(HttpServletRequest request,
      @Valid @RequestBody YarnRequest yarnRequest,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка создания пряжи: name={}, material={}, IP={}",
        yarnRequest.getName(), yarnRequest.getMaterial(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при создании пряжи: {} ошибок, IP={}", errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/yarns"));
    }

    try {
      YarnResponse response = yarnService.create(yarnRequest);
      log.info("Пряжа успешно создана: id={}, name={}, IP={}",
          response.getId(), response.getName(), clientIP);
      return ResponseEntity.ok(response);
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при создании пряжи: IP={}, error={}", clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/yarns"));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при создании пряжи: IP={}, error={}", clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to create yarn",
          "path", "/api/yarns"));
    }
  }

  @GetMapping
  @Operation(summary = "Получить список пряжи", description = "Возвращает весь каталог пряжи с возможностью фильтрации")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список пряжи получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = YarnResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка в параметрах фильтрации", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> getAll(HttpServletRequest request,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String material) {
    String clientIP = request.getRemoteAddr();
    log.debug("Запрос списка пряжи: category={}, brand={}, material={}, IP={}",
        category, brand, material, clientIP);

    try {
      List<YarnResponse> response;
      if (category != null || brand != null || material != null) {
        response = yarnService.filter(category, brand, material);
        log.info("Список пряжи получен с фильтрацией: {} записей, IP={}", response.size(), clientIP);
      } else {
        response = yarnService.getAll();
        log.info("Список пряжи получен: {} записей, IP={}", response.size(), clientIP);
      }
      return ResponseEntity.ok(response);
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при получении списка пряжи: IP={}, error={}", clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to get yarn list",
          "path", "/api/yarns"));
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить пряжу по ID", description = "Возвращает детальную информацию о пряже")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа найдена", content = @Content(schema = @Schema(implementation = YarnResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пряжа не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> getById(HttpServletRequest request, @PathVariable Long id) {
    String clientIP = request.getRemoteAddr();
    log.debug("Запрос пряжи по ID: id={}, IP={}", id, clientIP);

    try {
      YarnResponse response = yarnService.getById(id);
      log.info("Пряжа найдена: id={}, name={}, IP={}", id, response.getName(), clientIP);
      return ResponseEntity.ok(response);
    } catch (ResponseStatusException ex) {
      log.warn("Пряжа не найдена: id={}, IP={}, error={}", id, clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/yarns/" + id));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при получении пряжи: id={}, IP={}, error={}", id, clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to get yarn",
          "path", "/api/yarns/" + id));
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Обновить пряжу", description = "Обновляет существующую пряжу в каталоге")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа успешно обновлена", content = @Content(schema = @Schema(implementation = YarnResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пряжа не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> update(HttpServletRequest request,
      @PathVariable Long id,
      @Valid @RequestBody YarnRequest yarnRequest,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка обновления пряжи: id={}, name={}, IP={}",
        id, yarnRequest.getName(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при обновлении пряжи: id={}, {} ошибок, IP={}",
          id, errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/yarns/" + id));
    }

    try {
      YarnResponse response = yarnService.update(id, yarnRequest);
      log.info("Пряжа успешно обновлена: id={}, name={}, IP={}",
          id, response.getName(), clientIP);
      return ResponseEntity.ok(response);
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при обновлении пряжи: id={}, IP={}, error={}", id, clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/yarns/" + id));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при обновлении пряжи: id={}, IP={}, error={}",
          id, clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to update yarn",
          "path", "/api/yarns/" + id));
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Удалить пряжу", description = "Удаляет пряжу по её ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа успешно удалена"),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пряжа не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка удаления пряжи: id={}, IP={}", id, clientIP);

    try {
      yarnService.delete(id);
      log.info("Пряжа успешно удалена: id={}, IP={}", id, clientIP);
      return ResponseEntity.ok(Map.of(
          "timestamp", LocalDateTime.now(),
          "message", "Yarn deleted successfully",
          "id", id));
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при удалении пряжи: id={}, IP={}, error={}", id, clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/yarns/" + id));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при удалении пряжи: id={}, IP={}, error={}",
          id, clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to delete yarn",
          "path", "/api/yarns/" + id));
    }
  }
}
