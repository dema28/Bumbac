package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.CollectionRequest;
import com.bumbac.modules.catalog.dto.CollectionResponse;
import com.bumbac.modules.catalog.entity.Collection;
import com.bumbac.modules.catalog.mapper.CollectionMapper;
import com.bumbac.modules.catalog.repository.CollectionRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Collection Management", description = "API для управления коллекциями пряжи")
@Slf4j
public class CollectionController {

  private final CollectionRepository collectionRepository;
  private final CollectionMapper collectionMapper;

  @GetMapping
  @Operation(summary = "Получить список коллекций", description = "Возвращает список всех коллекций пряжи")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список коллекций получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @Transactional(readOnly = true)
  public ResponseEntity<?> getAll(HttpServletRequest request) {
    String clientIP = request.getRemoteAddr();
    log.debug("Запрос списка коллекций: IP={}", clientIP);

    try {
      List<CollectionResponse> response = collectionRepository.findAll().stream()
          .map(collectionMapper::toResponse)
          .toList();

      log.info("Список коллекций получен: {} записей, IP={}", response.size(), clientIP);
      return ResponseEntity.ok(response);

    } catch (Exception ex) {
      log.error("Неожиданная ошибка при получении списка коллекций: IP={}, error={}",
          clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to get collections list",
          "path", "/api/collections"));
    }
  }

  @PostMapping
  @Operation(summary = "Создать коллекцию", description = "Добавляет новую коллекцию в систему")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Коллекция успешно создана", content = @Content(schema = @Schema(implementation = CollectionResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Коллекция с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<?> create(HttpServletRequest request,
      @Valid @RequestBody CollectionRequest collectionRequest,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка создания коллекции: name={}, IP={}",
        collectionRequest.getName(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при создании коллекции: {} ошибок, IP={}", errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/collections"));
    }

    try {
      // Проверка уникальности имени
      if (collectionRepository.existsByName(collectionRequest.getName())) {
        log.warn("Попытка создать коллекцию с дублирующимся именем: name={}, IP={}",
            collectionRequest.getName(), clientIP);
        return ResponseEntity.status(409).body(Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 409,
            "error", "Conflict",
            "message", "Collection with name '" + collectionRequest.getName() + "' already exists",
            "path", "/api/collections"));
      }

      Collection saved = collectionRepository.save(collectionMapper.toEntity(collectionRequest));
      CollectionResponse response = collectionMapper.toResponse(saved);

      log.info("Коллекция успешно создана: id={}, name={}, IP={}",
          saved.getId(), saved.getName(), clientIP);

      return ResponseEntity.ok(response);

    } catch (Exception ex) {
      log.error("Неожиданная ошибка при создании коллекции: name={}, IP={}, error={}",
          collectionRequest.getName(), clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to create collection",
          "path", "/api/collections"));
    }
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить коллекцию", description = "Обновляет существующую коллекцию")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Коллекция успешно обновлена", content = @Content(schema = @Schema(implementation = CollectionResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Коллекция не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Коллекция с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<?> update(HttpServletRequest request,
      @PathVariable Long id,
      @Valid @RequestBody CollectionRequest collectionRequest,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка обновления коллекции: id={}, name={}, IP={}",
        id, collectionRequest.getName(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при обновлении коллекции: id={}, {} ошибок, IP={}",
          id, errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/collections/" + id));
    }

    try {
      Collection existingCollection = collectionRepository.findById(id)
          .orElseThrow(() -> {
            log.warn("Попытка обновить несуществующую коллекцию: id={}, IP={}", id, clientIP);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
          });

      // Проверка уникальности имени (исключая текущую коллекцию)
      if (!existingCollection.getName().equals(collectionRequest.getName()) &&
          collectionRepository.existsByNameAndIdNot(collectionRequest.getName(), id)) {
        log.warn("Попытка обновить коллекцию с дублирующимся именем: id={}, name={}, IP={}",
            id, collectionRequest.getName(), clientIP);
        return ResponseEntity.status(409).body(Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 409,
            "error", "Conflict",
            "message", "Collection with name '" + collectionRequest.getName() + "' already exists",
            "path", "/api/collections/" + id));
      }

      existingCollection.setName(collectionRequest.getName());
      existingCollection.setDescription(collectionRequest.getDescription());

      Collection updatedCollection = collectionRepository.save(existingCollection);
      CollectionResponse response = collectionMapper.toResponse(updatedCollection);

      log.info("Коллекция успешно обновлена: id={}, name={}, IP={}",
          id, updatedCollection.getName(), clientIP);

      return ResponseEntity.ok(response);

    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/collections/" + id));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при обновлении коллекции: id={}, IP={}, error={}",
          id, clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to update collection",
          "path", "/api/collections/" + id));
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить коллекцию", description = "Удаляет коллекцию по ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Коллекция успешно удалена"),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Коллекция не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Коллекция используется в пряже", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка удаления коллекции: id={}, IP={}", id, clientIP);

    try {
      Collection collection = collectionRepository.findById(id)
          .orElseThrow(() -> {
            log.warn("Попытка удалить несуществующую коллекцию: id={}, IP={}", id, clientIP);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found");
          });

      // Проверка, не используется ли коллекция в пряже
      if (!collection.getYarns().isEmpty()) {
        log.warn("Попытка удалить коллекцию, которая используется в пряже: id={}, yarnsCount={}, IP={}",
            id, collection.getYarns().size(), clientIP);
        return ResponseEntity.status(409).body(Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 409,
            "error", "Conflict",
            "message", "Cannot delete collection that is used by yarns",
            "path", "/api/collections/" + id));
      }

      collectionRepository.deleteById(id);

      log.info("Коллекция успешно удалена: id={}, IP={}", id, clientIP);
      return ResponseEntity.ok(Map.of(
          "timestamp", LocalDateTime.now(),
          "message", "Collection deleted successfully",
          "id", id));

    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/collections/" + id));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при удалении коллекции: id={}, IP={}, error={}",
          id, clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to delete collection",
          "path", "/api/collections/" + id));
    }
  }
}
