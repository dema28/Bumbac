package com.bumbac.modules.order.entity;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.shared.entity.BaseEntity;
import com.bumbac.shared.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user_id", columnList = "user_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Заказ пользователя")
public class Order extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "Пользователь обязателен")
  @Schema(description = "Пользователь, создавший заказ", requiredMode = Schema.RequiredMode.REQUIRED)
  private User user;

  @Column(nullable = false, precision = 12, scale = 2)
  @NotNull(message = "Общая сумма обязательна")
  @Positive(message = "Общая сумма должна быть положительной")
  @Schema(description = "Общая сумма заказа", example = "299.90", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  @NotNull(message = "Статус заказа обязателен")
  @Schema(description = "Статус заказа", example = "NEW", requiredMode = Schema.RequiredMode.REQUIRED)
  private OrderStatus status;

  @JsonManagedReference
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Schema(description = "Список товаров в заказе")
  private List<OrderItem> items;

  @Column(length = 1000)
  @Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
  @Schema(description = "Комментарий к заказу", example = "Доставить до 18:00")
  private String comment;

  @Column(name = "preferred_delivery_date", length = 10)
  @Size(max = 10, message = "Дата доставки не должна превышать 10 символов")
  @Schema(description = "Предпочитаемая дата доставки", example = "2024-01-20")
  private String preferredDeliveryDate;

  @Column(name = "delivered_at")
  @Schema(description = "Дата и время доставки", example = "2024-01-20T14:00:00")
  private LocalDateTime deliveredAt;

  @Column(name = "shipping_address", length = 500)
  @Size(max = 500, message = "Адрес доставки не должен превышать 500 символов")
  @Schema(description = "Адрес доставки", example = "ул. Пушкина, д. 10, кв. 5")
  private String shippingAddress;

  @Column(name = "contact_phone", length = 20)
  @Size(max = 20, message = "Телефон не должен превышать 20 символов")
  @Schema(description = "Контактный телефон", example = "+37360123456")
  private String contactPhone;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (status == null) {
      status = OrderStatus.NEW;
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
