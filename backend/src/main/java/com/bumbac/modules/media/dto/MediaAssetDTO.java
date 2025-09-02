package com.bumbac.modules.media.dto;

import com.bumbac.modules.media.entity.MediaFormat;
import com.bumbac.modules.media.entity.MediaVariant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "DTO объекта медиафайла")
public class MediaAssetDTO {

  @Schema(description = "Уникальный идентификатор медиафайла", example = "1")
  private Long id;

  @Schema(description = "URL файла", example = "http://example.com/uploads/image1.jpg")
  private String url;

  @Schema(description = "Альтернативный текст для изображения", example = "Картинка мотка пряжи")
  private String altText;

  @Schema(description = "Тип варианта медиа (основной, миниатюра и т.п.)", example = "MAIN")
  private MediaVariant variant;

  @Schema(description = "Формат медиафайла (JPG, PNG и т.д.)", example = "JPG")
  private MediaFormat format;

  @Schema(description = "Ширина изображения в пикселях", example = "800")
  private Integer widthPx;

  @Schema(description = "Высота изображения в пикселях", example = "600")
  private Integer heightPx;

  @Schema(description = "Размер файла в байтах", example = "1024000")
  private Integer sizeBytes;

  @Schema(description = "Порядок сортировки", example = "1")
  private Integer sortOrder;

  @Schema(description = "Дата создания", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;
}
