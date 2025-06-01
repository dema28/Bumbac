package com.bumbac.catalog.media;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
