package com.bumbac.modules.catalog.entity;

import com.bumbac.shared.entity.BaseEntity;
// ⬇️ было: import com.bumbac.modules.auth.entity.UserFavorite;
import com.bumbac.modules.user.entity.UserFavorite; // ✅ правильный импорт
import com.bumbac.modules.cart.entity.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yarns", indexes = {
        @Index(name = "idx_yarn_name", columnList = "name"),
        @Index(name = "idx_yarn_material", columnList = "material"),
        @Index(name = "idx_yarn_brand_id", columnList = "brand_id"),
        @Index(name = "idx_yarn_category_id", columnList = "category_id"),
        @Index(name = "idx_yarn_collection_id", columnList = "collection_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Пряжа в каталоге")
public class Yarn extends BaseEntity {

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "Название пряжи", example = "Super Soft Cotton", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Material is required")
    @Size(max = 100, message = "Material must not exceed 100 characters")
    @Schema(description = "Материал пряжи", example = "100% Cotton", requiredMode = Schema.RequiredMode.REQUIRED)
    private String material;

    @Column(nullable = false)
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @DecimalMin(value = "1.0", message = "Weight must be at least 1 gram")
    @Schema(description = "Вес мотка в граммах", example = "50.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double weight;

    @Column(nullable = false)
    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    @DecimalMin(value = "1.0", message = "Length must be at least 1 meter")
    @Schema(description = "Длина пряжи в метрах", example = "150.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double length;

    @Column(name = "pricemdl", nullable = false)
    @NotNull(message = "Price in MDL is required")
    @PositiveOrZero(message = "Price in MDL must be zero or positive")
    @DecimalMin(value = "0.0", message = "Price in MDL must be non-negative")
    @Schema(description = "Цена в молдавских леях", example = "39.90", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double priceMDL;

    @Column(name = "priceusd", nullable = false)
    @NotNull(message = "Price in USD is required")
    @PositiveOrZero(message = "Price in USD must be zero or positive")
    @DecimalMin(value = "0.0", message = "Price in USD must be non-negative")
    @Schema(description = "Цена в долларах США", example = "2.25", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double priceUSD;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата создания записи", example = "2023-10-01T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Collection is required")
    @Schema(description = "Коллекция пряжи", requiredMode = Schema.RequiredMode.REQUIRED)
    private Collection collection;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    @NotNull(message = "Brand is required")
    @Schema(description = "Бренд пряжи", requiredMode = Schema.RequiredMode.REQUIRED)
    private Brand brand;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    @Schema(description = "Категория пряжи", requiredMode = Schema.RequiredMode.REQUIRED)
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Список избранных записей пользователей")
    private List<UserFavorite> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Переводы пряжи на разные языки")
    private List<YarnTranslation> translations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Доступные цвета пряжи")
    private List<Color> colors = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
