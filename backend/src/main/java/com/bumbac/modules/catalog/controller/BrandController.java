package com.bumbac.modules.catalog.controller;

import com.bumbac.modules.catalog.dto.BrandRequest;
import com.bumbac.modules.catalog.dto.BrandResponse;
import com.bumbac.modules.catalog.entity.Brand;
import com.bumbac.modules.catalog.mapper.BrandMapper;
import com.bumbac.modules.catalog.repository.BrandRepository;
import com.bumbac.modules.catalog.repository.YarnRepository;
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
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Brand Management", description = "API для управления брендами пряжи")
@Slf4j
public class BrandController {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final YarnRepository yarnRepository;

    @GetMapping
    @Operation(summary = "Получить список брендов", description = "Возвращает список всех брендов пряжи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список брендов получен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        log.debug("Запрос списка брендов: IP={}", clientIP);

        try {
            List<BrandResponse> response = brandRepository.findAll().stream()
                    .map(brandMapper::toResponse)
                    .toList();

            log.info("Список брендов получен: {} записей, IP={}", response.size(), clientIP);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Неожиданная ошибка при получении списка брендов: IP={}, error={}",
                    clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to get brands list",
                    "path", "/api/brands"));
        }
    }

    @PostMapping
    @Operation(summary = "Создать бренд", description = "Добавляет новый бренд в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бренд успешно создан", content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Бренд с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> create(HttpServletRequest request,
                                    @Valid @RequestBody BrandRequest brandRequest,
                                    BindingResult result) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка создания бренда: name={}, IP={}",
                brandRequest.getName(), clientIP);

        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            log.warn("Ошибка валидации при создании бренда: {} ошибок, IP={}", errors.size(), clientIP);
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors,
                    "path", "/api/brands"));
        }

        try {
            if (brandRepository.existsByName(brandRequest.getName())) {
                log.warn("Попытка создать бренд с дублирующимся именем: name={}, IP={}",
                        brandRequest.getName(), clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Brand with name '" + brandRequest.getName() + "' already exists",
                        "path", "/api/brands"));
            }

            Brand saved = brandRepository.save(brandMapper.toEntity(brandRequest));
            BrandResponse response = brandMapper.toResponse(saved);

            log.info("Бренд успешно создан: id={}, name={}, IP={}",
                    saved.getId(), saved.getName(), clientIP);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Неожиданная ошибка при создании бренда: name={}, IP={}, error={}",
                    brandRequest.getName(), clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to create brand",
                    "path", "/api/brands"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить бренд", description = "Обновляет существующий бренд")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бренд успешно обновлен", content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бренд не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Бренд с таким именем уже существует", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> update(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @Valid @RequestBody BrandRequest brandRequest,
                                    BindingResult result) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка обновления бренда: id={}, name={}, IP={}",
                id, brandRequest.getName(), clientIP);

        if (result.hasErrors()) {
            var errors = result.getFieldErrors().stream()
                    .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                    .toList();
            log.warn("Ошибка валидации при обновлении бренда: id={}, {} ошибок, IP={}",
                    id, errors.size(), clientIP);
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 400,
                    "error", "Validation failed",
                    "messages", errors,
                    "path", "/api/brands/" + id));
        }

        try {
            Brand existingBrand = brandRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Попытка обновить несуществующий бренд: id={}, IP={}", id, clientIP);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found");
                    });

            // Проверка уникальности имени (исключая текущий бренд)
            if (!existingBrand.getName().equals(brandRequest.getName()) &&
                    brandRepository.existsByNameAndIdNot(brandRequest.getName(), id)) {
                log.warn("Попытка обновить бренд с дублирующимся именем: id={}, name={}, IP={}",
                        id, brandRequest.getName(), clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Brand with name '" + brandRequest.getName() + "' already exists",
                        "path", "/api/brands/" + id));
            }

            existingBrand.setName(brandRequest.getName());
            // Обновляем реальные поля сущности (при наличии в запросе)
            if (brandRequest.getCountry() != null) {
                existingBrand.setCountry(brandRequest.getCountry());
            }
            if (brandRequest.getWebsite() != null) {
                existingBrand.setWebsite(brandRequest.getWebsite());
            }

            Brand updatedBrand = brandRepository.save(existingBrand);
            BrandResponse response = brandMapper.toResponse(updatedBrand);

            log.info("Бренд успешно обновлен: id={}, name={}, IP={}",
                    id, updatedBrand.getName(), clientIP);

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException ex) {
            assert ex.getReason() != null;
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/brands/" + id));
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при обновлении бренда: id={}, IP={}, error={}",
                    id, clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to update brand",
                    "path", "/api/brands/" + id));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить бренд", description = "Удаляет бренд по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бренд успешно удален"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бренд не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Бренд используется в пряже", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable Long id) {
        String clientIP = request.getRemoteAddr();
        log.info("Попытка удаления бренда: id={}, IP={}", id, clientIP);

        try {
            Brand brand = brandRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Попытка удалить несуществующий бренд: id={}, IP={}", id, clientIP);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found");
                    });

            // Проверяем использование бренда без загрузки коллекций
            if (yarnRepository.existsByBrand_Id(id)) {
                long cnt = yarnRepository.countByBrand_Id(id); // опционально для логов
                log.warn("Попытка удалить бренд, который используется в пряже: id={}, yarnsCount={}, IP={}",
                        id, cnt, clientIP);
                return ResponseEntity.status(409).body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", "Cannot delete brand that is used by yarns",
                        "path", "/api/brands/" + id));
            }

            brandRepository.deleteById(id);

            log.info("Бренд успешно удален: id={}, IP={}", id, clientIP);
            return ResponseEntity.ok(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "message", "Brand deleted successfully",
                    "id", id));

        } catch (ResponseStatusException ex) {
            assert ex.getReason() != null;
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", ex.getStatusCode().value(),
                    "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
                    "message", ex.getReason(),
                    "path", "/api/brands/" + id));
        } catch (Exception ex) {
            log.error("Неожиданная ошибка при удалении бренда: id={}, IP={}, error={}",
                    id, clientIP, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Failed to delete brand",
                    "path", "/api/brands/" + id));
        }
    }
}
