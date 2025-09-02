package com.bumbac.modules.admin.controller;

import com.bumbac.modules.admin.dto.UserAdminDTO;
import com.bumbac.modules.admin.dto.UpdateRoleRequest;
import com.bumbac.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bumbac.core.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "API для административных функций")
public class AdminController {

  private final AdminService adminService;

  @Operation(summary = "Получить список всех пользователей", description = "Возвращает полный список зарегистрированных пользователей. Доступно только для роли ADMIN.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен", content = @Content(schema = @Schema(implementation = UserAdminDTO.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён — недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/users")
  public ResponseEntity<List<UserAdminDTO>> getAllUsers() {
    return ResponseEntity.ok(adminService.getAllUsers());
  }

  @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID. Доступно только для роли ADMIN.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён — недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    adminService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Изменить роль пользователя", description = "Позволяет изменить роль пользователя на USER, ADMIN или MODERATOR. Только для роли ADMIN.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Роль пользователя успешно обновлена"),
      @ApiResponse(responseCode = "400", description = "Некорректное значение роли", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Неавторизованный доступ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Доступ запрещён — недостаточно прав", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PutMapping("/users/{id}/role")
  public ResponseEntity<UserAdminDTO> updateUserRole(
      @PathVariable Long id,
      @Valid @RequestBody UpdateRoleRequest request) {
    UserAdminDTO updatedUser = adminService.updateRole(id, request.getRoleCode());
    return ResponseEntity.ok(updatedUser);
  }
}
