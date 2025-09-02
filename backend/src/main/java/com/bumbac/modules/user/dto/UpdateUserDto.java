package com.bumbac.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление профиля пользователя")
public class UpdateUserDto {

  @NotBlank(message = "Имя обязательно для заполнения")
  @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
  @Pattern(regexp = "^[а-яА-Яa-zA-Z\\s-']+$", message = "Имя может содержать только буквы, пробелы, дефисы и апострофы")
  @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
  private String firstName;

  @NotBlank(message = "Фамилия обязательна для заполнения")
  @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
  @Pattern(regexp = "^[а-яА-Яa-zA-Z\\s-']+$", message = "Фамилия может содержать только буквы, пробелы, дефисы и апострофы")
  @Schema(description = "Фамилия пользователя", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
  private String lastName;

  @NotBlank(message = "Телефон обязателен для заполнения")
  @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{10,20}$", message = "Некорректный формат телефона")
  @Size(min = 10, max = 20, message = "Телефон должен содержать от 10 до 20 символов")
  @Schema(description = "Номер телефона", example = "+37360123456", requiredMode = Schema.RequiredMode.REQUIRED)
  private String phone;

  @Email(message = "Некорректный формат email")
  @Size(max = 255, message = "Email не должен превышать 255 символов")
  @Schema(description = "Email пользователя", example = "ivan@example.com")
  private String email;
}
