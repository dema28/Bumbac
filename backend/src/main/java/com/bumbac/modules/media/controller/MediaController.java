package com.bumbac.modules.media.controller;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.dto.UploadMediaRequest;
import com.bumbac.modules.media.service.MediaAssetService;
import com.bumbac.modules.media.entity.MediaEntityType;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Media", description = "API для управления медиафайлами")
@Slf4j
public class MediaController {

  private final MediaAssetService mediaAssetService;

  @GetMapping
  @Operation(summary = "Получить медиафайлы для сущности", description = "Возвращает список всех прикреплённых медиафайлов для заданной сущности (тип и ID)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Медиафайлы получены", content = @Content(schema = @Schema(implementation = MediaAssetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<MediaAssetDTO>> getMedia(
      @RequestParam("type") @NotNull MediaEntityType type,
      @RequestParam("id") @NotNull @Positive Long entityId) {

    log.debug("Получение медиафайлов для сущности типа {} с ID {}", type, entityId);
    List<MediaAssetDTO> media = mediaAssetService.getMediaForEntity(type, entityId);
    return ResponseEntity.ok(media);
  }

  @PostMapping("/upload")
  @Operation(summary = "Загрузить файл", description = "Загружает медиафайл на сервер и возвращает информацию о загруженном файле")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Файл успешно загружен", content = @Content(schema = @Schema(implementation = MediaAssetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации файла", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Ошибка сервера при загрузке файла", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<MediaAssetDTO> uploadFile(
      @RequestParam("file") MultipartFile file,
      @Valid UploadMediaRequest request) {

    log.info("Попытка загрузки файла: {} для сущности типа {} с ID {}",
        file.getOriginalFilename(), request.getEntityType(), request.getEntityId());

    try {
      MediaAssetDTO uploadedMedia = mediaAssetService.uploadFile(file, request);
      log.info("Файл {} успешно загружен", file.getOriginalFilename());
      return ResponseEntity.status(HttpStatus.CREATED).body(uploadedMedia);

    } catch (Exception e) {
      log.error("Ошибка при загрузке файла {}: {}", file.getOriginalFilename(), e.getMessage(), e);
      throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить медиафайл", description = "Удаляет медиафайл по его ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Медиафайл успешно удалён"),
      @ApiResponse(responseCode = "404", description = "Медиафайл не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Void> deleteMedia(@PathVariable @Positive Long id) {
    log.info("Попытка удаления медиафайла с ID {}", id);
    mediaAssetService.deleteMedia(id);
    log.info("Медиафайл с ID {} успешно удалён", id);
    return ResponseEntity.noContent().build();
  }
}
