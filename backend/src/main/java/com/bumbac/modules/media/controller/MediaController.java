package com.bumbac.modules.media.controller;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.service.MediaAssetService;
import com.bumbac.modules.media.entity.MediaEntityType;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MediaController {

    private final MediaAssetService mediaAssetService;

    @GetMapping
    @Operation(summary = "Получить медиафайлы для сущности", description = "Возвращает список всех прикреплённых медиафайлов для заданной сущности (тип и ID)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Медиафайлы получены"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<MediaAssetDTO> getMedia(
            @RequestParam("type") MediaEntityType type,
            @RequestParam("id") Long entityId) {
        return mediaAssetService.getMediaForEntity(type, entityId);
    }

    @PostMapping("/upload")
    @Operation(summary = "Загрузить файл", description = "Загружает медиафайл на сервер и возвращает ссылку на него")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при загрузке файла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "/volume1/web/uploads/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://qscfgrt657.duckdns.org/uploads/" + filename;
            return Map.of("status", "success", "url", fileUrl);

        } catch (IOException e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
