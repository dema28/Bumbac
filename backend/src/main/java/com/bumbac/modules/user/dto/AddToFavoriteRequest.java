package com.bumbac.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление конкретного цвета пряжи в избранное")
public class AddToFavoriteRequest {

    @NotNull(message = "ID цвета обязателен")
    @Positive(message = "ID цвета должен быть положительным числом")
    @Schema(description = "ID конкретного цвета пряжи для добавления в избранное", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long colorId;

    @Size(max = 500, message = "Заметки не должны превышать 500 символов")
    @Schema(description = "Заметки к избранному", example = "Хочу связать свитер из этого цвета")
    private String notes;
}