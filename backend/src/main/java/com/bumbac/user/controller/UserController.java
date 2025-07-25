package com.bumbac.user.controller;

import com.bumbac.auth.entity.User;
import com.bumbac.common.dto.ErrorResponse;
import com.bumbac.user.dto.UpdateUserDto;
import com.bumbac.user.dto.UserProfileRequest;
import com.bumbac.user.dto.UserProfileResponse;
import com.bumbac.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  /**
   * ✅ Получение текущего пользователя (полный профиль с ролями)
   */
  @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем авторизованном пользователе, включая его роли.")
  @ApiResponse(responseCode = "200", description = "Успешный ответ с профилем пользователя")
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.getCurrentUser(userDetails.getUsername());
    var dto = new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getRoleCodes(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone());
    return ResponseEntity.ok(dto);
  }

  /**
   * ✅ Обновление профиля текущего пользователя
   */
  @Operation(summary = "Обновить профиль", description = "Позволяет текущему пользователю обновить имя, фамилию и телефон.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Профиль успешно обновлён"),
          @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/profile")
  @RequestBody(description = "Обновляемые данные пользователя", required = true,
          content = @Content(schema = @Schema(implementation = UpdateUserDto.class)))
  public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody UpdateUserDto dto) {
    userService.updateUserProfile(userDetails.getUsername(), dto);
    return ResponseEntity.ok("Profile updated");
  }

  /**
   * ✅ Смена пароля текущего пользователя
   */
  @Operation(summary = "Сменить пароль", description = "Позволяет текущему пользователю сменить свой пароль.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Пароль успешно обновлён"),
          @ApiResponse(responseCode = "400", description = "Некорректный пароль",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/password")
  public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String newPassword) {
    userService.changePassword(userDetails.getUsername(), newPassword);
    return ResponseEntity.ok("Password updated");
  }

  /**
   * ✅ Получение всех пользователей (доступно только администратору)
   */
  @Operation(summary = "Получить всех пользователей (только для ADMIN)", description = "Возвращает список всех пользователей. Доступно только для роли ADMIN.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
          @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin/all")
  public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAll());
  }

  /**
   * ✅ Удаление пользователя (только админ)
   */
  @Operation(summary = "Удалить пользователя по ID (только для ADMIN)", description = "Удаляет пользователя по ID. Доступно только для роли ADMIN.")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
          @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/admin/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
