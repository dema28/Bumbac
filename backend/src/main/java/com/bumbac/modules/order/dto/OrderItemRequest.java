package com.bumbac.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Товар в заказе")
public class OrderItemRequest {

    @NotNull(message = "ID цвета обязателен")
    @Positive(message = "ID цвета должен быть положительным числом")
    @Schema(description = "ID цвета товара", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long colorId;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть положительным числом")
    @Schema(description = "Количество товара", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
