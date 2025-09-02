package com.bumbac.modules.pattern.entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "pattern_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Перевод схемы вязания на конкретный язык")
public class PatternTranslation {

  @Schema(description = "Составной ключ перевода")
  @EmbeddedId
  private PatternTranslationId id;

  @Schema(description = "Схема вязания")
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("patternId")
  @JoinColumn(name = "pattern_id", nullable = false)
  private Pattern pattern;

  @Schema(description = "Название схемы на указанном языке")
  @Column(nullable = false, length = 100)
  private String name;

  @Schema(description = "Описание схемы на указанном языке")
  @Column(length = 1000)
  private String description;
}
