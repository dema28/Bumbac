package com.bumbac.modules.pattern.entity;

import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Entity
@Table(name = "patterns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность схемы вязания")
public class Pattern extends BaseEntity {

  @Schema(description = "Уникальный идентификатор")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Schema(description = "Уникальный код схемы")
  @Column(nullable = false, unique = true, length = 20)
  private String code;

  @Schema(description = "Связанная пряжа")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "yarn_id", nullable = false)
  private Yarn yarn;

  @Schema(description = "URL к PDF файлу схемы")
  @Column(name = "pdf_url", length = 500)
  private String pdfUrl;

  @Schema(description = "Уровень сложности схемы")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Difficulty difficulty;

  @Schema(description = "Переводы схемы на разные языки")
  @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PatternTranslation> translations;
}
