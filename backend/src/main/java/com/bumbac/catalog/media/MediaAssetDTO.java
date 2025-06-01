package com.bumbac.catalog.media;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaAssetDTO {
    private String url;
    private String altText;
    private MediaVariant variant;
    private MediaFormat format;
    private Integer widthPx;
    private Integer heightPx;
}
