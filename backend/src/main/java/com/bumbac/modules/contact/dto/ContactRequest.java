package com.bumbac.modules.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на отправку контактного сообщения")
public class ContactRequest {

  @NotBlank(message = "Имя обязательно для заполнения")
  @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
  @Schema(description = "Имя отправителя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank(message = "Email обязателен для заполнения")
  @Email(message = "Некорректный формат email")
  @Size(max = 255, message = "Email не должен превышать 255 символов")
  @Schema(description = "Email отправителя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @NotBlank(message = "Тема сообщения обязательна для заполнения")
  @Size(min = 5, max = 200, message = "Тема должна содержать от 5 до 200 символов")
  @Schema(description = "Тема сообщения", example = "Вопрос о товаре", requiredMode = Schema.RequiredMode.REQUIRED)
  private String subject;

  @NotBlank(message = "Текст сообщения обязателен для заполнения")
  @Size(min = 10, max = 5000, message = "Сообщение должно содержать от 10 до 5000 символов")
  @Schema(description = "Текст сообщения", example = "Здравствуйте! У меня есть вопрос о вашем товаре...", requiredMode = Schema.RequiredMode.REQUIRED)
  private String message;
}
