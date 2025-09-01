package com.bumbac.modules.order.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.order.dto.CreateOrderRequest;
import com.bumbac.modules.order.dto.OrderResponse;
import com.bumbac.modules.order.service.OrderService;
import com.bumbac.shared.enums.OrderStatus;
import com.bumbac.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Заказы", description = "API для управления заказами")
@Slf4j
public class OrderController {

  private final OrderService orderService;
  private final UserService userService;

  @PostMapping
  @Operation(summary = "Оформить заказ", description = "Создаёт новый заказ для текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Заказ успешно оформлен", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации или корзина пуста", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Корзина не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<OrderResponse> placeOrder(
      @Valid @RequestBody CreateOrderRequest request,
      BindingResult bindingResult,
      @AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на создание заказа от IP: {}", clientIP);

    if (bindingResult.hasErrors()) {
      log.warn("Ошибка валидации при создании заказа: {}", bindingResult.getAllErrors());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Ошибка валидации: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    try {
      User user = userService.getCurrentUser(userDetails.getUsername());
      OrderResponse response = orderService.placeOrder(user, request);

      log.info("Заказ успешно создан: id={}, пользователь={}", response.getId(), user.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при создании заказа: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при создании заказа");
    }
  }

  @PostMapping("/from-cart")
  @Operation(summary = "Оформить заказ из корзины", description = "Создаёт заказ из содержимого корзины пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Заказ успешно оформлен", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "400", description = "Корзина пуста", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Корзина не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<OrderResponse> placeOrderFromCart(
      @AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на создание заказа из корзины от IP: {}", clientIP);

    try {
      User user = userService.getCurrentUser(userDetails.getUsername());
      OrderResponse response = orderService.placeOrderFromCart(user);

      log.info("Заказ из корзины успешно создан: id={}, пользователь={}", response.getId(), user.getEmail());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при создании заказа из корзины: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при создании заказа из корзины");
    }
  }

  @GetMapping
  @Operation(summary = "Получить заказы пользователя", description = "Возвращает список заказов текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список заказов получен", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<OrderResponse>> getUserOrders(
      @AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение заказов пользователя от IP: {}", clientIP);

    try {
      User user = userService.getCurrentUser(userDetails.getUsername());
      List<OrderResponse> orders = orderService.getUserOrders(user);

      log.debug("Возвращено {} заказов для пользователя: {}", orders.size(), user.getEmail());
      return ResponseEntity.ok(orders);

    } catch (Exception e) {
      log.error("Ошибка при получении заказов пользователя: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении заказов");
    }
  }

  @GetMapping("/{orderId}")
  @Operation(summary = "Получить заказ по ID", description = "Возвращает конкретный заказ по идентификатору")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Заказ найден", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<OrderResponse> getOrderById(
      @PathVariable @NotNull(message = "ID заказа обязателен") @Positive(message = "ID заказа должен быть положительным числом") Long orderId,
      @AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение заказа ID: {} от IP: {}", orderId, clientIP);

    try {
      User user = userService.getCurrentUser(userDetails.getUsername());
      OrderResponse order = orderService.getOrderById(orderId, user);

      log.debug("Заказ ID: {} найден для пользователя: {}", orderId, user.getEmail());
      return ResponseEntity.ok(order);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при получении заказа ID {}: {}", orderId, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении заказа");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/all")
  @Operation(summary = "Получить все заказы", description = "Доступно только администраторам")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список всех заказов получен", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<OrderResponse>> getAllOrders(HttpServletRequest httpRequest) {
    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на получение всех заказов от IP: {}", clientIP);

    try {
      List<OrderResponse> orders = orderService.getAllOrders();
      log.debug("Возвращено {} заказов", orders.size());
      return ResponseEntity.ok(orders);

    } catch (Exception e) {
      log.error("Ошибка при получении всех заказов: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении заказов");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/status/{status}")
  @Operation(summary = "Получить заказы по статусу", description = "Возвращает заказы с указанным статусом")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Заказы по статусу получены", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный статус", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
      @PathVariable @NotNull(message = "Статус обязателен") OrderStatus status,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение заказов по статусу: {} от IP: {}", status, clientIP);

    try {
      List<OrderResponse> orders = orderService.getOrdersByStatus(status);
      log.debug("Возвращено {} заказов со статусом: {}", orders.size(), status);
      return ResponseEntity.ok(orders);

    } catch (Exception e) {
      log.error("Ошибка при получении заказов по статусу {}: {}", status, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении заказов по статусу");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{orderId}/status")
  @Operation(summary = "Обновить статус заказа", description = "Изменяет статус заказа (только для администраторов)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Статус заказа обновлен", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный статус", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Заказ не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @PathVariable @NotNull(message = "ID заказа обязателен") @Positive(message = "ID заказа должен быть положительным числом") Long orderId,
      @RequestParam @NotNull(message = "Новый статус обязателен") OrderStatus status,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.info("Запрос на обновление статуса заказа ID: {} на статус: {} от IP: {}", orderId, status, clientIP);

    try {
      OrderResponse order = orderService.updateOrderStatus(orderId, status);
      log.info("Статус заказа ID: {} успешно обновлен на: {}", orderId, status);
      return ResponseEntity.ok(order);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Ошибка при обновлении статуса заказа ID {}: {}", orderId, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при обновлении статуса заказа");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/stats/count/{status}")
  @Operation(summary = "Получить количество заказов по статусу", description = "Возвращает количество заказов с указанным статусом")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Количество заказов получено", content = @Content(schema = @Schema(example = "{\"status\": \"NEW\", \"count\": 5}"))),
      @ApiResponse(responseCode = "400", description = "Некорректный статус", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<Map<String, Object>> getOrderCountByStatus(
      @PathVariable @NotNull(message = "Статус обязателен") OrderStatus status,
      HttpServletRequest httpRequest) {

    String clientIP = getClientIP(httpRequest);
    log.debug("Запрос на получение количества заказов по статусу: {} от IP: {}", status, clientIP);

    try {
      long count = orderService.getOrderCountByStatus(status);
      Map<String, Object> stats = Map.of(
          "status", status.toString(),
          "count", count);

      log.debug("Количество заказов со статусом {}: {}", status, count);
      return ResponseEntity.ok(stats);

    } catch (Exception e) {
      log.error("Ошибка при получении количества заказов по статусу {}: {}", status, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Внутренняя ошибка при получении статистики заказов");
    }
  }

  // Вспомогательный метод для получения IP-адреса клиента
  private String getClientIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIP = request.getHeader("X-Real-IP");
    if (xRealIP != null && !xRealIP.isEmpty()) {
      return xRealIP;
    }

    return request.getRemoteAddr();
  }
}
