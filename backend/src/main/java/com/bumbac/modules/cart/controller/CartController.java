package com.bumbac.modules.cart.controller;

import com.bumbac.modules.cart.dto.AddToCartRequest;
import com.bumbac.modules.cart.dto.UpdateCartRequest;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.service.CartService;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "Добавить товар в корзину", description = "Добавляет товар в корзину пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар добавлен в корзину"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> addItem(HttpServletRequest request,
                                     @RequestBody AddToCartRequest dto) {
        cartService.addItem(request, dto);
        return ResponseEntity.ok("Added to cart");
    }


    @PutMapping
    @Operation(summary = "Обновить товар в корзине", description = "Обновляет количество товара в корзине")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Товар не найден в корзине",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateItem(
            HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestBody UpdateCartRequest dto
    ) {
        cartService.updateItem(request, dto);
        return ResponseEntity.ok("Cart updated");
    }


    @GetMapping
    @Operation(summary = "Получить содержимое корзины", description = "Возвращает все товары в корзине пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Содержимое корзины",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartItem.class)))
    })
    public ResponseEntity<List<CartItem>> getItems(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getItems(request));
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина очищена")
    })
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        cartService.clearCart(request);
        return ResponseEntity.ok("Cart cleared");
    }
}
