package com.bumbac.modules.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление товара в корзине")
public class UpdateCartRequest {

  @NotNull(message = "ID цвета обязателен")
  @Min(value = 1, message = "ID цвета должен быть положительным числом")
  @Schema(description = "ID цвета товара", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long colorId;

  @NotNull(message = "Количество обязательно")
  @Min(value = 0, message = "Количество не может быть отрицательным")
  @Schema(description = "Новое количество товара (0 для удаления)", example = "3", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer quantity;
}
