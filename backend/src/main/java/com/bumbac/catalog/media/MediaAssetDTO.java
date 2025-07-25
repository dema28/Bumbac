package com.bumbac.catalog.media;

import com.bumbac.catalog.media.MediaFormat;
import com.bumbac.catalog.media.MediaVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO объекта медиафайла")
public class MediaAssetDTO {

    @Schema(description = "URL файла", example = "http://example.com/uploads/image1.jpg")
    private String url;

    @Schema(description = "Альтернативный текст для изображения", example = "Картинка мотка пряжи")
    private String altText;

    @Schema(description = "Тип варианта медиа (основной, миниатюра и т.п.)")
    private MediaVariant variant;

    @Schema(description = "Формат медиафайла (JPG, PNG и т.д.)")
    private MediaFormat format;

    @Schema(description = "Ширина изображения в пикселях", example = "800")
    private Integer widthPx;

    @Schema(description = "Высота изображения в пикселях", example = "600")
    private Integer heightPx;
}
