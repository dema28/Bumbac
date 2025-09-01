package com.bumbac.modules.user.controller;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.core.dto.ErrorResponse;
import com.bumbac.modules.user.dto.ChangePasswordRequest;
import com.bumbac.modules.user.dto.UpdateUserDto;
import com.bumbac.modules.user.dto.UserProfileResponse;
import com.bumbac.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "API для управления профилем пользователя")
@Slf4j
public class UserController {

  private final UserService userService;

  /**
   * Получение текущего пользователя (полный профиль с ролями)
   */
  @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем авторизованном пользователе, включая его роли.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Успешный ответ с профилем пользователя", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Запрос профиля текущего пользователя от IP: {}", clientIP);

    try {
      User user = userService.getCurrentUser(userDetails.getUsername());
      UserProfileResponse response = UserProfileResponse.builder()
          .id(user.getId())
          .email(user.getEmail())
          .roles(user.getRoleCodes())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .phone(user.getPhone())
          .createdAt(user.getCreatedAt())
          .updatedAt(user.getUpdatedAt())
          .favoritesCount(userService.getFavoritesCount(user.getId()))
          .ordersCount(0) // TODO: добавить подсчет заказов
          .build();

      log.debug("Профиль пользователя получен: id={}, email={}", user.getId(), user.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка при получении профиля пользователя: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Обновление профиля текущего пользователя
   */
  @Operation(summary = "Обновить профиль", description = "Позволяет текущему пользователю обновить имя, фамилию, телефон и email.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Профиль успешно обновлён", content = @Content(schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = "{\"message\": \"Profile updated successfully\"}"))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/profile")
  @RequestBody(description = "Обновляемые данные пользователя", required = true, content = @Content(schema = @Schema(implementation = UpdateUserDto.class)))
  public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Valid UpdateUserDto dto,
      BindingResult bindingResult,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Обновление профиля пользователя от IP: {}", clientIP);

    if (bindingResult.hasErrors()) {
      log.warn("Ошибки валидации при обновлении профиля: {}", bindingResult.getAllErrors());
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Validation failed", "details", bindingResult.getAllErrors()));
    }

    try {
      userService.updateUserProfile(userDetails.getUsername(), dto);
      log.info("Профиль пользователя обновлен: {}", userDetails.getUsername());
      return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));

    } catch (Exception e) {
      log.error("Ошибка при обновлении профиля: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Смена пароля текущего пользователя
   */
  @Operation(summary = "Сменить пароль", description = "Позволяет текущему пользователю сменить свой пароль.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Пароль успешно обновлён", content = @Content(schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = "{\"message\": \"Password updated successfully\"}"))),
      @ApiResponse(responseCode = "400", description = "Ошибка валидации или неверный старый пароль", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/password")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Старый и новый пароли", required = true, content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class), examples = {
      @ExampleObject(name = "valid", value = "{\"oldPassword\":\"OldStrong1\",\"newPassword\":\"NewStrong1\"}"),
      @ExampleObject(name = "too-short", value = "{\"oldPassword\":\"x\",\"newPassword\":\"Poke\"}")
  }))
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,
      @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid ChangePasswordRequest req,
      BindingResult bindingResult,
      HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Смена пароля пользователя от IP: {}", clientIP);

    if (bindingResult.hasErrors()) {
      log.warn("Ошибки валидации при смене пароля: {}", bindingResult.getAllErrors());
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Validation failed", "details", bindingResult.getAllErrors()));
    }

    try {
      userService.changePassword(userDetails.getUsername(), req.oldPassword(), req.newPassword());
      log.info("Пароль пользователя изменен: {}", userDetails.getUsername());
      return ResponseEntity.ok(Map.of("message", "Password updated successfully"));

    } catch (Exception e) {
      log.error("Ошибка при смене пароля: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Получение всех пользователей (доступно только администратору)
   */
  @Operation(summary = "Получить всех пользователей (только для ADMIN)", description = "Возвращает список всех пользователей. Доступно только для роли ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/all")
  public ResponseEntity<List<UserProfileResponse>> getAllUsers(HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Запрос всех пользователей от IP: {} (ADMIN)", clientIP);

    try {
      List<UserProfileResponse> users = userService.getAll();
      log.debug("Получено {} пользователей", users.size());
      return ResponseEntity.ok(users);

    } catch (Exception e) {
      log.error("Ошибка при получении всех пользователей: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Удаление пользователя (только админ)
   */
  @Operation(summary = "Удалить пользователя по ID (только для ADMIN)", description = "Удаляет пользователя по ID. Доступно только для роли ADMIN.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/admin/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
    String clientIP = getClientIP(request);
    log.info("Удаление пользователя {} от IP: {} (ADMIN)", id, clientIP);

    try {
      userService.deleteById(id);
      log.info("Пользователь удален: id={}", id);
      return ResponseEntity.noContent().build();

    } catch (Exception e) {
      log.error("Ошибка при удалении пользователя {}: {}", id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
