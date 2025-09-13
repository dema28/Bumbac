package com.bumbac.modules.user.entity;

import com.bumbac.modules.cart.entity.Color;
import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.auth.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorites", indexes = {
        @Index(name = "idx_user_favorite_user_id", columnList = "user_id"),
        @Index(name = "idx_user_favorite_color_id", columnList = "color_id"), // ИСПРАВЛЕНО: color_id
        @Index(name = "idx_user_favorite_added_at", columnList = "added_at"),
        @Index(name = "idx_user_favorite_user_color", columnList = "user_id, color_id", unique = true) // ИСПРАВЛЕНО
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Schema(description = "Избранный цвет пряжи пользователя")
public class UserFavorite {

    @EmbeddedId
    @Schema(description = "Составной ключ (user_id + color_id)")
    private UserFavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Пользователь", requiredMode = Schema.RequiredMode.REQUIRED)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colorId")
    @JoinColumn(name = "color_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Конкретный цвет пряжи", requiredMode = Schema.RequiredMode.REQUIRED)
    private Color color;

    // Получаем пряжу через цвет
    public Yarn getYarn() {
        return color != null ? color.getYarn() : null;
    }

    @Column(name = "added_at", nullable = false)
    @NotNull(message = "Дата добавления обязательна")
    @Schema(description = "Дата добавления в избранное", example = "2024-01-15T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime addedAt;

    @Column(length = 500)
    @Schema(description = "Заметки пользователя к избранному", example = "Хочу связать свитер из этого цвета")
    private String notes;

    // Добавляем поля из BaseEntity вручную
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата создания записи")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Дата последнего обновления")
    private LocalDateTime updatedAt;

    // ИСПРАВЛЕННЫЙ конструктор для удобства
    public UserFavorite(User user, Color color, String notes) {
        this.user = user;
        this.color = color; // ИСПРАВЛЕНО: используем color, а не yarn
        this.notes = notes;
        this.id = new UserFavoriteId(user.getId(), color.getId()); // ИСПРАВЛЕНО: color.getId()
        this.addedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (addedAt == null) {
            addedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}