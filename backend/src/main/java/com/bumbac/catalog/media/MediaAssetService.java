package com.bumbac.catalog.media;

import java.util.List;

public interface MediaAssetService {
    List<MediaAssetDTO> getMediaForEntity(MediaEntityType type, Long entityId);
}
