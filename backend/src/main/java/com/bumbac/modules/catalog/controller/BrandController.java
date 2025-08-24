package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.BrandRequest;
import com.bumbac.modules.catalog.dto.BrandResponse;
import com.bumbac.modules.catalog.entity.Brand;
import com.bumbac.modules.catalog.mapper.BrandMapper;
import com.bumbac.modules.catalog.repository.BrandRepository;
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
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Operation(summary = "Получить список брендов", description = "Возвращает список всех брендов пряжи")
    @ApiResponse(responseCode = "200", description = "Список брендов получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BrandResponse.class)))
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAll() {
        List<BrandResponse> response = brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Создать новый бренд", description = "Добавляет новый бренд пряжи в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бренд успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные нового бренда", required = true,
            content = @Content(schema = @Schema(implementation = BrandRequest.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        Brand saved = brandRepository.save(brandMapper.toEntity(request));
        return ResponseEntity.ok(brandMapper.toResponse(saved));
    }

    @Operation(summary = "Удалить бренд", description = "Удаляет бренд по его ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Бренд удалён"),
            @ApiResponse(responseCode = "404", description = "Бренд не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!brandRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
