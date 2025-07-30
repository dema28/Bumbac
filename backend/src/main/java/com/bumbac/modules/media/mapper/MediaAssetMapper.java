package com.bumbac.modules.media.mapper;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.entity.MediaAsset;
import org.springframework.stereotype.Component;

@Component
public class MediaAssetMapper {

    public MediaAssetDTO toDto(MediaAsset entity) {
        return MediaAssetDTO.builder()
                .url(entity.getUrl())
                .altText(entity.getAltText())
                .variant(entity.getVariant())
                .format(entity.getFormat())
                .widthPx(entity.getWidthPx() != null ? entity.getWidthPx().intValue() : 0)
                .heightPx(entity.getHeightPx() != null ? entity.getHeightPx().intValue() : 0)
                .build();
    }

}
