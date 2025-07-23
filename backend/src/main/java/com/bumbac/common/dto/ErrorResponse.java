package com.bumbac.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Структура ответа об ошибке")
public class ErrorResponse {

    @Schema(description = "Дата и время ошибки", example = "2025-07-23T20:05:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус", example = "404")
    private int status;

    @Schema(description = "Название ошибки", example = "Not Found")
    private String error;

    @Schema(description = "Подробное сообщение об ошибке", example = "Пользователь не найден")
    private String message;

    @Schema(description = "Путь, на котором произошла ошибка", example = "/api/admin/users/999")
    private String path;
}
