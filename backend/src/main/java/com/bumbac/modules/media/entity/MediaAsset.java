package com.bumbac.modules.media.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MediaEntityType entityType;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private MediaVariant variant;

    @Enumerated(EnumType.STRING)
    private MediaFormat format;

    private String url;
    private Short widthPx;
    private Short heightPx;
    private Integer sizeBytes;
    private String altText;

    private Integer sortOrder;

    private LocalDateTime createdAt;
}
