package com.bumbac.modules.media.mapper;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.entity.MediaAsset;
import org.springframework.stereotype.Component;

@Component
public class MediaAssetMapper {

  public MediaAssetDTO toDto(MediaAsset entity) {
    return MediaAssetDTO.builder()
        .id(entity.getId())
        .url(entity.getUrl())
        .altText(entity.getAltText())
        .variant(entity.getVariant())
        .format(entity.getFormat())
        .widthPx(entity.getWidthPx() != null ? entity.getWidthPx().intValue() : null)
        .heightPx(entity.getHeightPx() != null ? entity.getHeightPx().intValue() : null)
        .sizeBytes(entity.getSizeBytes())
        .sortOrder(entity.getSortOrder())
        .createdAt(entity.getCreatedAt())
        .build();
  }
}
