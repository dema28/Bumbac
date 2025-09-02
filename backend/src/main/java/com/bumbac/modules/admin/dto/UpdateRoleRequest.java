package com.bumbac.modules.admin.dto;

import com.bumbac.shared.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление роли пользователя")
public class UpdateRoleRequest {

  @NotBlank(message = "Код роли обязателен")
  @Pattern(regexp = "^(USER|ADMIN|MODERATOR)$", message = "Роль должна быть одной из: USER, ADMIN, MODERATOR")
  @Schema(description = "Код новой роли пользователя", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
      "USER", "ADMIN", "MODERATOR" })
  private String roleCode;

  public UserRole getRole() {
    return UserRole.fromCode(roleCode);
  }
}
