package com.bumbac.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными товара в заказе")
public class OrderItemResponse {

  @Schema(description = "Уникальный идентификатор товара в заказе", example = "1")
  private Long id;

  @Schema(description = "ID цвета товара", example = "1")
  private Long colorId;

  @Schema(description = "Название цвета", example = "Красный")
  private String colorName;

  @Schema(description = "Название пряжи", example = "Super Soft Cotton")
  private String yarnName;

  @Schema(description = "Количество товара", example = "2")
  private Integer quantity;

  @Schema(description = "Цена за единицу", example = "49.95")
  private BigDecimal unitPrice;

  @Schema(description = "Общая стоимость товара", example = "99.90")
  private BigDecimal totalPrice;

  @Schema(description = "Изображение товара", example = "https://example.com/image.jpg")
  private String imageUrl;
}
