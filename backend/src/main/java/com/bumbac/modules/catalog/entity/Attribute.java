package com.bumbac.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Атрибут пряжи")
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор атрибута")
    private Long id;

    @Column(nullable = false, length = 128, unique = true)
    @Schema(description = "Название атрибута", example = "Thickness")
    private String name;

    @Column(nullable = false, length = 128, unique = true)
    @Schema(description = "URL-slug атрибута", example = "thickness")
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    @Schema(description = "Тип данных атрибута")
    private DataType dataType;

    public enum DataType {
        TEXT,       // Текстовое значение
        NUMBER,     // Числовое значение
        BOOLEAN,    // Логическое значение
        ENUM        // Перечисление (список значений)
    }
}