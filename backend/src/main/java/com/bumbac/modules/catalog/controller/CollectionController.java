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
@RequestMapping("/api/collections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;

    @GetMapping
    @Operation(summary = "Получить список коллекций", description = "Возвращает список всех коллекций пряжи")
    @ApiResponse(responseCode = "200", description = "Список коллекций получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionResponse.class)))
    public ResponseEntity<List<CollectionResponse>> getAll() {
        List<CollectionResponse> response = collectionRepository.findAll().stream()
                .map(collectionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Создать коллекцию", description = "Добавляет новую коллекцию в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Коллекция успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные новой коллекции", required = true,
            content = @Content(schema = @Schema(implementation = CollectionRequest.class)))
    public ResponseEntity<CollectionResponse> create(@Valid @RequestBody CollectionRequest request) {
        Collection saved = collectionRepository.save(collectionMapper.toEntity(request));
        return ResponseEntity.ok(collectionMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить коллекцию", description = "Удаляет коллекцию по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Коллекция удалена"),
            @ApiResponse(responseCode = "404", description = "Коллекция не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!collectionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        collectionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
