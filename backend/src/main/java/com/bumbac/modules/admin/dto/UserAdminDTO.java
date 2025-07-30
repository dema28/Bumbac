package com.bumbac.modules.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Информация о пользователе для администратора")
public class UserAdminDTO {

  @Schema(description = "Уникальный идентификатор пользователя", example = "1")
  private Long id;

  @Schema(description = "Email пользователя", example = "admin@example.com")
  private String email;

  @Schema(description = "Имя пользователя", example = "Denis")
  private String firstName;

  @Schema(description = "Фамилия пользователя", example = "Novicov")
  private String lastName;

  @Schema(description = "Список ролей пользователя", example = "[\"USER\", \"ADMIN\"]")
  private List<String> roles;
}
