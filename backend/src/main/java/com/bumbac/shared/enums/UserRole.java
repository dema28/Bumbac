package com.bumbac.shared.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Роли пользователей в системе")
public enum UserRole {

  @Schema(description = "Обычный пользователь")
  USER("USER"),

  @Schema(description = "Модератор")
  MODERATOR("MODERATOR"),

  @Schema(description = "Администратор")
  ADMIN("ADMIN");

  private final String code;

  UserRole(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static UserRole fromCode(String code) {
    for (UserRole role : values()) {
      if (role.code.equalsIgnoreCase(code)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Неизвестная роль: " + code);
  }

  public static boolean isValidRole(String code) {
    try {
      fromCode(code);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
