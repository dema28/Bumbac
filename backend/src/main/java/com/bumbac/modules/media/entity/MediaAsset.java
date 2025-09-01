package com.bumbac.modules.media.entity;

import com.bumbac.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность медиафайла")
public class MediaAsset extends BaseEntity {

  @Schema(description = "Уникальный идентификатор")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Schema(description = "Тип сущности, к которой прикреплен медиафайл")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaEntityType entityType;

  @Schema(description = "ID сущности, к которой прикреплен медиафайл")
  @Column(nullable = false)
  private Long entityId;

  @Schema(description = "Вариант медиафайла (основной, миниатюра и т.п.)")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaVariant variant;

  @Schema(description = "Формат медиафайла")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaFormat format;

  @Schema(description = "URL медиафайла")
  @Column(nullable = false, length = 500)
  private String url;

  @Schema(description = "Ширина изображения в пикселях")
  @Column(name = "width_px")
  private Short widthPx;

  @Schema(description = "Высота изображения в пикселях")
  @Column(name = "height_px")
  private Short heightPx;

  @Schema(description = "Размер файла в байтах")
  @Column(name = "size_bytes")
  private Integer sizeBytes;

  @Schema(description = "Альтернативный текст для изображения")
  @Column(name = "alt_text", length = 255)
  private String altText;

  @Schema(description = "Порядок сортировки медиафайла")
  @Column(name = "sort_order")
  private Integer sortOrder;

  @Schema(description = "Дата создания медиафайла")
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
