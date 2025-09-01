package com.bumbac.modules.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на фильтрацию контактных сообщений")
public class ContactFilterRequest {

  @Schema(description = "Дата начала периода (формат: yyyy-MM-dd)", example = "2024-01-01")
  @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате yyyy-MM-dd")
  private String from;

  @Schema(description = "Дата окончания периода (формат: yyyy-MM-dd)", example = "2024-01-31")
  @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате yyyy-MM-dd")
  private String to;

  @Schema(description = "Email отправителя для фильтрации", example = "ivan@example.com")
  private String email;

  @Schema(description = "Тема сообщения для фильтрации", example = "Вопрос")
  private String subject;
}
