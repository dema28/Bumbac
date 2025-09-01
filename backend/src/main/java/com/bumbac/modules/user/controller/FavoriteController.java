package com.bumbac.modules.user.controller;

import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.user.dto.AddToFavoriteRequest;
import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.user.service.FavoriteService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Favorites Management", description = "API для управления избранными товарами")
@Slf4j
public class FavoriteController {

  private final FavoriteService favoriteService;

  @GetMapping
  @Operation(summary = "Получить избранное текущего пользователя", description = "Возвращает список избранной пряжи текущего авторизованного пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список избранного получен", content = @Content(schema = @Schema(implementation = FavoriteDTO.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<List<FavoriteDTO>> getFavorites(@AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Запрос избранного от IP: {}", clientIP);

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      List<FavoriteDTO> favorites = favoriteService.getFavoritesForUser(userId);

      log.debug("Получено {} избранных товаров для пользователя {}", favorites.size(), userId);
      return ResponseEntity.ok(favorites);

    } catch (Exception e) {
      log.error("Ошибка при получении избранного: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{userId}")
  @Operation(summary = "Получить избранное пользователя (только для ADMIN)", description = "Возвращает список избранной пряжи указанного пользователя. Доступно только для роли ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список избранного получен", content = @Content(schema = @Schema(implementation = FavoriteDTO.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<FavoriteDTO>> getFavoritesForUser(@PathVariable Long userId,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Запрос избранного пользователя {} от IP: {} (ADMIN)", userId, clientIP);

    try {
      List<FavoriteDTO> favorites = favoriteService.getFavoritesForUser(userId);

      log.debug("Получено {} избранных товаров для пользователя {}", favorites.size(), userId);
      return ResponseEntity.ok(favorites);

    } catch (Exception e) {
      log.error("Ошибка при получении избранного пользователя {}: {}", userId, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping
  @Operation(summary = "Добавить в избранное", description = "Добавляет пряжу в список избранного текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа добавлена в избранное", content = @Content(schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"Added to favorites successfully\"}"))),
      @ApiResponse(responseCode = "400", description = "Ошибка запроса или товар уже в избранном", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пряжа не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> addToFavorites(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Valid AddToFavoriteRequest request,
      BindingResult bindingResult,
      HttpServletRequest httpRequest) {
    String clientIP = getClientIP(httpRequest);
    log.info("Добавление в избранное от IP: {}", clientIP);

    if (bindingResult.hasErrors()) {
      log.warn("Ошибки валидации при добавлении в избранное: {}", bindingResult.getAllErrors());
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Validation failed", "details", bindingResult.getAllErrors()));
    }

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      favoriteService.addToFavorites(userId, request);

      log.info("Товар добавлен в избранное: userId={}, yarnId={}", userId, request.getYarnId());
      return ResponseEntity.ok(Map.of("message", "Added to favorites successfully"));

    } catch (Exception e) {
      log.error("Ошибка при добавлении в избранное: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  @PostMapping("/{yarnId}")
  @Operation(summary = "Добавить пряжу в избранное по ID", description = "Добавляет пряжу по ID в список избранного текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа добавлена в избранное", content = @Content(schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"Added to favorites successfully\"}"))),
      @ApiResponse(responseCode = "400", description = "Ошибка запроса или товар уже в избранном", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пряжа не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> addToFavoritesById(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long yarnId,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Добавление в избранное по ID от IP: {}, yarnId={}", clientIP, yarnId);

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      favoriteService.addToFavorites(userId, yarnId);

      log.info("Товар добавлен в избранное: userId={}, yarnId={}", userId, yarnId);
      return ResponseEntity.ok(Map.of("message", "Added to favorites successfully"));

    } catch (Exception e) {
      log.error("Ошибка при добавлении в избранное: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  @DeleteMapping("/{yarnId}")
  @Operation(summary = "Удалить из избранного", description = "Удаляет пряжу из избранного текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пряжа удалена из избранного", content = @Content(schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"message\": \"Removed from favorites successfully\"}"))),
      @ApiResponse(responseCode = "400", description = "Ошибка запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Товар не найден в избранном", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> removeFromFavorites(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long yarnId,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Удаление из избранного от IP: {}, yarnId={}", clientIP, yarnId);

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      favoriteService.removeFromFavorites(userId, yarnId);

      log.info("Товар удален из избранного: userId={}, yarnId={}", userId, yarnId);
      return ResponseEntity.ok(Map.of("message", "Removed from favorites successfully"));

    } catch (Exception e) {
      log.error("Ошибка при удалении из избранного: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  @GetMapping("/check/{yarnId}")
  @Operation(summary = "Проверить, есть ли товар в избранном", description = "Проверяет, добавлена ли пряжа в избранное текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Результат проверки", content = @Content(schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"inFavorites\": true}"))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> checkIfInFavorites(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long yarnId,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.debug("Проверка избранного от IP: {}, yarnId={}", clientIP, yarnId);

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      boolean inFavorites = favoriteService.isInFavorites(userId, yarnId);

      log.debug("Проверка избранного: userId={}, yarnId={}, result={}", userId, yarnId, inFavorites);
      return ResponseEntity.ok(Map.of("inFavorites", inFavorites));

    } catch (Exception e) {
      log.error("Ошибка при проверке избранного: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  @GetMapping("/count")
  @Operation(summary = "Получить количество избранных товаров", description = "Возвращает количество товаров в избранном текущего пользователя")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Количество избранных товаров", content = @Content(schema = @Schema(implementation = Map.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"count\": 5}"))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public ResponseEntity<?> getFavoritesCount(@AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.debug("Запрос количества избранного от IP: {}", clientIP);

    try {
      // TODO: извлечь userId из JWT токена
      Long userId = 1L; // Временная заглушка
      long count = favoriteService.getFavoritesCount(userId);

      log.debug("Количество избранного для пользователя {}: {}", userId, count);
      return ResponseEntity.ok(Map.of("count", count));

    } catch (Exception e) {
      log.error("Ошибка при получении количества избранного: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  // Вспомогательные методы
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
