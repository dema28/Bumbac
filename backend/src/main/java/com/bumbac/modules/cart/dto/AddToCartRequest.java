package com.bumbac.modules.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на добавление товара в корзину")
public class AddToCartRequest {

  @NotNull(message = "ID цвета обязателен")
  @Min(value = 1, message = "ID цвета должен быть положительным числом")
  @Schema(description = "ID цвета товара", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long colorId;

  @NotNull(message = "Количество обязательно")
  @Min(value = 1, message = "Количество должно быть больше 0")
  @Schema(description = "Количество товара для добавления", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer quantity;
}
