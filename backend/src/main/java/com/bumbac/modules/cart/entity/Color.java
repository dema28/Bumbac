package com.bumbac.modules.cart.entity;

import com.bumbac.modules.catalog.entity.Yarn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "colors", indexes = {
    @Index(name = "idx_color_yarn_id", columnList = "yarn_id"),
    @Index(name = "idx_color_code", columnList = "colorCode"),
    @Index(name = "idx_color_sku", columnList = "sku")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Цвет/вариант товара")
public class Color {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Уникальный идентификатор цвета", example = "1")
  private Long id;

  @Column(nullable = false, length = 20)
  @NotBlank(message = "Код цвета обязателен")
  @Schema(description = "Код цвета", example = "RED001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
  private String colorCode;

  @Column(nullable = false, length = 100)
  @NotBlank(message = "Название цвета обязательно")
  @Schema(description = "Название цвета", example = "Красный", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
  private String colorName;

  @Column(unique = true, nullable = false, length = 50)
  @NotBlank(message = "SKU обязателен")
  @Schema(description = "Артикул товара", example = "YARN-RED-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
  private String sku;

  @Column(length = 100)
  @Schema(description = "Штрихкод товара", example = "1234567890123", maxLength = 100)
  private String barcode;

  @Column(length = 7)
  @Schema(description = "Цвет в HEX формате", example = "#FF0000", pattern = "^#[0-9A-Fa-f]{6}$", maxLength = 7)
  private String hexValue;

  @Column(nullable = false)
  @NotNull(message = "Количество на складе обязательно")
  @Min(value = 0, message = "Количество не может быть отрицательным")
  @Schema(description = "Количество на складе", example = "10", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer stockQuantity;

  @Column(nullable = false, precision = 10, scale = 2)
  @NotNull(message = "Цена обязательна")
  @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
  @Schema(description = "Цена товара", example = "25.99", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "yarn_id", nullable = false)
  @NotNull(message = "Пряжа обязательна")
  @Schema(description = "Пряжа, к которой относится этот цвет", hidden = true)
  private Yarn yarn;
}
