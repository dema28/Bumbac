package com.bumbac.order.controller;

import com.bumbac.auth.entity.User;
import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.order.entity.Order;
import com.bumbac.order.service.OrderService;
import com.bumbac.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Оформить заказ", description = "Создаёт новый заказ для текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ успешно оформлен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getCurrentUser(userDetails.getUsername());
        orderService.placeOrder(user);
        return ResponseEntity.ok("Order placed");
    }

    @GetMapping
    @Operation(summary = "Получить заказы пользователя", description = "Возвращает список заказов текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Получить все заказы", description = "Доступно только администраторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список всех заказов получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
