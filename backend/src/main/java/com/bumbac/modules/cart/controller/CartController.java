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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart", description = "API для управления корзиной покупок")
@Slf4j
public class CartController {

  private final CartService cartService;

  @PostMapping
  @Operation(summary = "Добавить товар в корзину", description = "Добавляет товар в корзину пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Товар добавлен в корзину"),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации или запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Товар не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Недостаточно товара на складе", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> addItem(HttpServletRequest request,
      @Valid @RequestBody AddToCartRequest dto,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка добавления товара в корзину: colorId={}, quantity={}, IP={}",
        dto.getColorId(), dto.getQuantity(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при добавлении в корзину: {} ошибок, IP={}", errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/cart"));
    }

    try {
      cartService.addItem(request, dto);
      log.info("Товар успешно добавлен в корзину: colorId={}, quantity={}, IP={}",
          dto.getColorId(), dto.getQuantity(), clientIP);
      return ResponseEntity.ok(Map.of(
          "timestamp", LocalDateTime.now(),
          "message", "Item added to cart successfully",
          "colorId", dto.getColorId(),
          "quantity", dto.getQuantity()));
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при добавлении товара в корзину: colorId={}, IP={}, error={}",
          dto.getColorId(), clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/cart"));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при добавлении товара в корзину: colorId={}, IP={}, error={}",
          dto.getColorId(), clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to add item to cart",
          "path", "/api/cart"));
    }
  }

  @PutMapping
  @Operation(summary = "Обновить товар в корзине", description = "Обновляет количество товара в корзине или удаляет его при quantity=0")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Корзина обновлена"),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации или запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Товар не найден в корзине", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> updateItem(HttpServletRequest request,
      @Valid @RequestBody UpdateCartRequest dto,
      BindingResult result) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка обновления товара в корзине: colorId={}, quantity={}, IP={}",
        dto.getColorId(), dto.getQuantity(), clientIP);

    if (result.hasErrors()) {
      var errors = result.getFieldErrors().stream()
          .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
          .toList();
      log.warn("Ошибка валидации при обновлении корзины: {} ошибок, IP={}", errors.size(), clientIP);
      return ResponseEntity.badRequest().body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 400,
          "error", "Validation failed",
          "messages", errors,
          "path", "/api/cart"));
    }

    try {
      cartService.updateItem(request, dto);
      log.info("Товар в корзине успешно обновлен: colorId={}, quantity={}, IP={}",
          dto.getColorId(), dto.getQuantity(), clientIP);

      String action = dto.getQuantity() == 0 ? "removed from" : "updated in";
      return ResponseEntity.ok(Map.of(
          "timestamp", LocalDateTime.now(),
          "message", "Item " + action + " cart successfully",
          "colorId", dto.getColorId(),
          "quantity", dto.getQuantity()));
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при обновлении товара в корзине: colorId={}, IP={}, error={}",
          dto.getColorId(), clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/cart"));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при обновлении товара в корзине: colorId={}, IP={}, error={}",
          dto.getColorId(), clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to update cart item",
          "path", "/api/cart"));
    }
  }

  @GetMapping
  @Operation(summary = "Получить содержимое корзины", description = "Возвращает все товары в корзине пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Содержимое корзины получено", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItem.class))),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Корзина пользователя не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> getItems(HttpServletRequest request) {
    String clientIP = request.getRemoteAddr();
    log.debug("Запрос содержимого корзины с IP: {}", clientIP);

    try {
      List<CartItem> items = cartService.getItems(request);
      log.info("Содержимое корзины получено: {} товаров, IP={}", items.size(), clientIP);
      return ResponseEntity.ok(items);
    } catch (ResponseStatusException ex) {
      log.warn("Ошибка при получении корзины: IP={}, error={}", clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/cart"));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при получении корзины: IP={}, error={}", clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to get cart items",
          "path", "/api/cart"));
    }
  }

  @DeleteMapping
  @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Корзина очищена"),
      @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Корзина пользователя не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> clearCart(HttpServletRequest request) {
    String clientIP = request.getRemoteAddr();
    log.info("Попытка очистки корзины с IP: {}", clientIP);

    try {
      cartService.clearCart(request);
      log.info("Корзина успешно очищена, IP: {}", clientIP);
      return ResponseEntity.ok(Map.of(
          "timestamp", LocalDateTime.now(),
          "message", "Cart cleared successfully"));
    } catch (ResponseStatusException ex) {
      log.error("Ошибка при очистке корзины: IP={}, error={}", clientIP, ex.getReason());
      return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", ex.getStatusCode().value(),
          "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
          "message", ex.getReason(),
          "path", "/api/cart"));
    } catch (Exception ex) {
      log.error("Неожиданная ошибка при очистке корзины: IP={}, error={}", clientIP, ex.getMessage(), ex);
      return ResponseEntity.status(500).body(Map.of(
          "timestamp", LocalDateTime.now(),
          "status", 500,
          "error", "Internal Server Error",
          "message", "Failed to clear cart",
          "path", "/api/cart"));
    }
  }
}
