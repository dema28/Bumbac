package com.bumbac.modules.user.entity;

import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.shared.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorites", indexes = {
    @Index(name = "idx_user_favorite_user_id", columnList = "user_id"),
    @Index(name = "idx_user_favorite_yarn_id", columnList = "yarn_id"),
    @Index(name = "idx_user_favorite_added_at", columnList = "added_at"),
    @Index(name = "idx_user_favorite_user_yarn", columnList = "user_id, yarn_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Избранная пряжа пользователя")
public class UserFavorite extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  @NotNull(message = "Пользователь обязателен")
  @Schema(description = "Пользователь", requiredMode = Schema.RequiredMode.REQUIRED)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "yarn_id", nullable = false)
  @JsonBackReference
  @NotNull(message = "Пряжа обязательна")
  @Schema(description = "Пряжа", requiredMode = Schema.RequiredMode.REQUIRED)
  private Yarn yarn;

  @Column(name = "added_at", nullable = false)
  @NotNull(message = "Дата добавления обязательна")
  @Schema(description = "Дата добавления в избранное", example = "2024-01-15T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDateTime addedAt;

  @Column(length = 500)
  @Schema(description = "Заметки пользователя к избранному", example = "Хочу связать свитер")
  private String notes;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (addedAt == null) {
      addedAt = LocalDateTime.now();
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
