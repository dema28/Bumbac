package com.bumbac.modules.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с информацией о корзине")
public class CartResponse {

    @Schema(description = "ID пользователя", example = "1")
    private Long userId;

    @Schema(description = "Список товаров в корзине")
    private List<CartItemResponse> items;
}


