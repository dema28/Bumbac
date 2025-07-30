package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.YarnRequest;
import com.bumbac.modules.catalog.dto.YarnResponse;
import com.bumbac.modules.catalog.service.YarnService;
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
@RequestMapping("/api/yarns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class YarnController {

    private final YarnService yarnService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать пряжу", description = "Добавляет новую пряжу в каталог")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пряжа успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Данные новой пряжи", required = true,
            content = @Content(schema = @Schema(implementation = YarnRequest.class)))
    public ResponseEntity<?> create(@Valid @RequestBody YarnRequest request) {
        yarnService.create(request);
        return ResponseEntity.ok("Yarn created");
    }

    @GetMapping
    @Operation(summary = "Получить список пряжи", description = "Возвращает весь каталог пряжи с возможностью фильтрации")
    @ApiResponse(responseCode = "200", description = "Список пряжи получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YarnResponse.class)))
    public ResponseEntity<List<YarnResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String material
    ) {
        if (category != null || brand != null || material != null) {
            return ResponseEntity.ok(yarnService.filter(category, brand, material));
        }

        return ResponseEntity.ok(yarnService.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пряжу", description = "Удаляет пряжу по её ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пряжа удалена"),
            @ApiResponse(responseCode = "404", description = "Пряжа не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        yarnService.delete(id);
        return ResponseEntity.ok("Yarn deleted");
    }
}
