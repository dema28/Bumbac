package com.bumbac.modules.pattern.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Составной ключ для перевода схемы вязания")
public class PatternTranslationId implements Serializable {

  @Schema(description = "ID схемы вязания")
  @Column(name = "pattern_id", nullable = false)
  private Long patternId;

  @Schema(description = "Код языка перевода")
  @Column(nullable = false, length = 2)
  private String locale;
}
