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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Получить список категорий", description = "Возвращает список всех категорий пряжи")
    @ApiResponse(responseCode = "200", description = "Список категорий получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class)))
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> response = categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Создать категорию", description = "Добавляет новую категорию в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные новой категории", required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequest.class)))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        return ResponseEntity.ok(categoryMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию", description = "Удаляет категорию по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категория удалена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
