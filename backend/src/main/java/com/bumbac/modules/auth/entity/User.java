package com.bumbac.modules.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.bumbac.modules.user.entity.UserFavorite;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Пользователь системы")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @JsonIgnore
    @ToString.Exclude
    @Column(name = "password_hash", nullable = false)
    @Schema(description = "Хеш пароля пользователя", hidden = true)
    private String passwordHash;

    @Column(name = "password_algo")
    @Schema(description = "Алгоритм хеширования пароля", example = "bcrypt")
    private String passwordAlgo;

    @Schema(description = "Имя пользователя", example = "John")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Doe")
    private String lastName;

    @Schema(description = "Номер телефона", example = "+37360123456")
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата создания аккаунта", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Дата последнего обновления аккаунта", example = "2024-01-20T12:00:00")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Избранные товары пользователя", hidden = true)
    private List<UserFavorite> favorites;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Роли пользователя", hidden = true)
    private Set<Role> roles;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    @Schema(description = "Флаг подтверждения email", example = "false")
    private boolean emailVerified = false;

    public List<String> getRoleCodes() {
        return roles == null ? List.of() : roles.stream().map(Role::getCode).toList();
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (updatedAt == null)
            updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
