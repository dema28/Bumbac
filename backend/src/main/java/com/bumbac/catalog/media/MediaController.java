package com.bumbac.catalog.media;

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
public class MediaController {

    private final MediaAssetService mediaAssetService;

    @GetMapping
    public List<MediaAssetDTO> getMedia(
            @RequestParam("type") MediaEntityType type,
            @RequestParam("id") Long entityId) {
        return mediaAssetService.getMediaForEntity(type, entityId);
    }
    @PostMapping("/upload")
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
