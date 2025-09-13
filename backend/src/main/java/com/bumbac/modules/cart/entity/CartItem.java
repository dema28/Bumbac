package com.bumbac.modules.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
        @Index(name = "idx_cart_item_color_id", columnList = "colorId") // ИЗМЕНЕНО: используем colorId
})
@Data
@NoArgsConstructor
@Schema(description = "Товар в корзине покупок")
public class CartItem {

    @EmbeddedId
    @Schema(description = "Составной ключ (cart_id + colorId)")
    private CartItemId id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id", nullable = false)
    @Schema(description = "Корзина, к которой принадлежит товар", hidden = true)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colorId")
    // ИСПРАВЛЕНО: убираем @JoinColumn, используем только @MapsId
    @Schema(description = "Цвет/вариант товара")
    private Color color;

    @Column(nullable = false)
    @NotNull(message = "Количество не может быть null")
    @Min(value = 1, message = "Количество должно быть больше 0")
    @Schema(description = "Количество товара в корзине", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Column(name = "added_at", nullable = false)
    @Schema(description = "Дата и время добавления товара в корзину")
    private LocalDateTime addedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата создания записи в корзине")
    private LocalDateTime createdAt;

    // Конструктор для создания нового товара в корзине
    public CartItem(Cart cart, Color color, Integer quantity, LocalDateTime addedAt) {
        this.cart = cart;
        this.color = color;
        this.quantity = quantity;
        this.addedAt = addedAt;
        // ДОБАВЛЕНО: создаем составной ключ
        this.id = new CartItemId(cart.getId(), color.getId());
    }

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}