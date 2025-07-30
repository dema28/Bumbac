package com.bumbac.modules.media.service;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.entity.MediaEntityType;

import java.util.List;

public interface MediaAssetService {
    List<MediaAssetDTO> getMediaForEntity(MediaEntityType type, Long entityId);
}
