package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.CategoryRequest;
import com.bumbac.modules.catalog.dto.CategoryResponse;
import com.bumbac.modules.catalog.entity.Category;
import com.bumbac.modules.catalog.mapper.CategoryMapper;
import com.bumbac.modules.catalog.repository.CategoryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Category Management", description = "API для управления категориями пряжи")
@Slf4j
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Получить список категорий", description = "Возвращает список всех категорий пряжи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список категорий получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        log.debug("Запрос списка категорий: IP={}", clientIP);

        try {
            List<CategoryResponse> response = categoryRepository.findAll().stream()
                    .map(categoryMapper::toResponse)
                    .toList();

            log.info("Список категорий получен: {} записей, IP={}", response.size(), clientIP);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Неожиданная ошибка при получении списка категорий: IP={}, error={}",
                    clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to get categories list",
                    "path", "/api/categories"));
        }
    }

    @PostMapping
    @Operation(summary = "Создать категорию", description = "Добавляет новую категорию в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно создана", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Категория с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> create(HttpServletRequest request,
                                    @Valid @RequestBody CategoryRequest categoryRequest,
                                    BindingResult result) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка создания категории: name={}, IP={}",
                categoryRequest.getName(), clientIP);

        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            log.warn("Ошибка валидации при создании категории: {} ошибок, IP={}", errors.size(), clientIP);
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors,
                    "path", "/api/categories"));
        }

        try {
            // Проверка уникальности имени
            if (categoryRepository.existsByName(categoryRequest.getName())) {
                log.warn("Попытка создать категорию с дублирующимся именем: name={}, IP={}",
                        categoryRequest.getName(), clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Category with name '" + categoryRequest.getName() + "' already exists",
                        "path", "/api/categories"));
            }

            Category saved = categoryRepository.save(categoryMapper.toEntity(categoryRequest));
            CategoryResponse response = categoryMapper.toResponse(saved);

            log.info("Категория успешно создана: id={}, name={}, IP={}",
                    saved.getId(), saved.getName(), clientIP);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Неожиданная ошибка при создании категории: name={}, IP={}, error={}",
                    categoryRequest.getName(), clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to create category",
                    "path", "/api/categories"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить категорию", description = "Обновляет существующую категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно обновлена", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Категория с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> update(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @Valid @RequestBody CategoryRequest categoryRequest,
                                    BindingResult result) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка обновления категории: id={}, name={}, IP={}",
                id, categoryRequest.getName(), clientIP);

        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            log.warn("Ошибка валидации при обновлении категории: id={}, {} ошибок, IP={}",
                    id, errors.size(), clientIP);
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors,
                    "path", "/api/categories/" + id));
        }

        try {
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Попытка обновить несуществующую категорию: id={}, IP={}", id, clientIP);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                    });

            // Проверка уникальности имени (исключая текущую категорию)
            if (!existingCategory.getName().equals(categoryRequest.getName()) &&
                    categoryRepository.existsByNameAndIdNot(categoryRequest.getName(), id)) {
                log.warn("Попытка обновить категорию с дублирующимся именем: id={}, name={}, IP={}",
                        id, categoryRequest.getName(), clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Category with name '" + categoryRequest.getName() + "' already exists",
                        "path", "/api/categories/" + id));
            }

            existingCategory.setName(categoryRequest.getName());
            // Временно закомментируем установку description, пока не добавим поле в Category entity
            // existingCategory.setDescription(categoryRequest.getDescription() != null ? categoryRequest.getDescription() : "");

            Category updatedCategory = categoryRepository.save(existingCategory);
            CategoryResponse response = categoryMapper.toResponse(updatedCategory);

            log.info("Категория успешно обновлена: id={}, name={}, IP={}",
                    id, updatedCategory.getName(), clientIP);

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/categories/" + id));
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при обновлении категории: id={}, IP={}, error={}",
                    id, clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to update category",
                    "path", "/api/categories/" + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию", description = "Удаляет категорию по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Категория используется в пряже", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка удаления категории: id={}, IP={}", id, clientIP);

        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Попытка удалить несуществующую категорию: id={}, IP={}", id, clientIP);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
                    });

            // Проверка, не используется ли категория в пряже (временно используем заглушку)
            List<Object> yarns = new ArrayList<>();  // Заглушка вместо category.getYarns()
            if (!yarns.isEmpty()) {
                log.warn("Попытка удалить категорию, которая используется в пряже: id={}, yarnsCount={}, IP={}",
                        id, yarns.size(), clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Cannot delete category that is used by yarns",
                        "path", "/api/categories/" + id));
            }

            categoryRepository.deleteById(id);

            log.info("Категория успешно удалена: id={}, IP={}", id, clientIP);
            return ResponseEntity.ok(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "message", "Category deleted successfully",
                    "id", id));

        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/categories/" + id));
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при удалении категории: id={}, IP={}, error={}",
                    id, clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to delete category",
                    "path", "/api/categories/" + id));
        }
    }
}