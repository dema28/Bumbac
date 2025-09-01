package com.bumbac.modules.order.entity;

import com.bumbac.modules.cart.entity.Color;
import com.bumbac.shared.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order_id", columnList = "order_id"),
    @Index(name = "idx_order_item_color_id", columnList = "color_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Товар в заказе")
public class OrderItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  @JsonBackReference
  @NotNull(message = "Заказ обязателен")
  @Schema(description = "Заказ, к которому относится товар", requiredMode = Schema.RequiredMode.REQUIRED)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "color_id", nullable = false)
  @NotNull(message = "Цвет товара обязателен")
  @Schema(description = "Цвет товара", requiredMode = Schema.RequiredMode.REQUIRED)
  private Color color;

  @Column(nullable = false)
  @NotNull(message = "Количество обязательно")
  @Positive(message = "Количество должно быть положительным числом")
  @Schema(description = "Количество товара", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer quantity;

  @Column(nullable = false, precision = 12, scale = 2)
  @NotNull(message = "Цена обязательна")
  @Positive(message = "Цена должна быть положительной")
  @Schema(description = "Цена за единицу товара", example = "49.95", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal unitPrice;

  @Column(nullable = false, precision = 12, scale = 2)
  @NotNull(message = "Общая стоимость обязательна")
  @Positive(message = "Общая стоимость должна быть положительной")
  @Schema(description = "Общая стоимость товара", example = "99.90", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal totalPrice;

  @Column(length = 500)
  @Schema(description = "Дополнительные заметки к товару", example = "Размер: L")
  private String notes;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = java.time.LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = java.time.LocalDateTime.now();
    }
    if (totalPrice == null && unitPrice != null && quantity != null) {
      totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = java.time.LocalDateTime.now();
    if (unitPrice != null && quantity != null) {
      totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }
}
