package com.bumbac.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание заказа")
public class CreateOrderRequest {

    @NotEmpty(message = "Список товаров не может быть пустым")
    @Schema(description = "Список товаров для заказа", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequest> items;

    @Schema(description = "Комментарий к заказу", example = "Доставить до 18:00")
    private String comment;

    @Schema(description = "Предпочитаемая дата доставки", example = "2024-01-20")
    private String preferredDeliveryDate;
}
