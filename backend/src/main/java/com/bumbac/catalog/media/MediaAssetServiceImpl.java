package com.bumbac.catalog.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaAssetServiceImpl implements MediaAssetService {

    private final MediaAssetRepository repository;
    private final MediaAssetMapper mapper;

    @Override
    public List<MediaAssetDTO> getMediaForEntity(MediaEntityType type, Long entityId) {
        return repository.findByEntityTypeAndEntityIdOrderBySortOrder(type, entityId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
