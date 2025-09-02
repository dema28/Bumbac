package com.bumbac.modules.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Ответ с информацией о товаре в корзине")
public class CartItemResponse {

  @Schema(description = "ID цвета товара", example = "1")
  private Long colorId;

  @Schema(description = "Код цвета", example = "RED001")
  private String colorCode;

  @Schema(description = "Название цвета", example = "Красный")
  private String colorName;

  @Schema(description = "SKU товара", example = "YARN-RED-001")
  private String sku;

  @Schema(description = "Цвет в HEX формате", example = "#FF0000")
  private String hexValue;

  @Schema(description = "Количество в корзине", example = "2")
  private Integer quantity;

  @Schema(description = "Цена за единицу", example = "25.99")
  private BigDecimal unitPrice;

  @Schema(description = "Общая стоимость", example = "51.98")
  private BigDecimal totalPrice;

  @Schema(description = "Доступное количество на складе", example = "10")
  private Integer stockQuantity;

  @Schema(description = "Дата добавления в корзину")
  private LocalDateTime addedAt;

  @Schema(description = "Название пряжи", example = "Merino Wool")
  private String yarnName;

  @Schema(description = "ID пряжи", example = "1")
  private Long yarnId;
}
