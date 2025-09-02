package com.bumbac.modules.order.entity;

import com.bumbac.modules.order.entity.Order;
import com.bumbac.shared.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.bumbac.shared.enums.ReturnStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "returns", indexes = {
    @Index(name = "idx_return_order_id", columnList = "order_id"),
    @Index(name = "idx_return_status", columnList = "status"),
    @Index(name = "idx_return_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Возврат товара")
public class Return extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  @NotNull(message = "Заказ обязателен")
  @Schema(description = "Заказ, к которому относится возврат", requiredMode = Schema.RequiredMode.REQUIRED)
  private Order order;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  @NotNull(message = "Статус возврата обязателен")
  @Schema(description = "Статус возврата", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
  private ReturnStatus status;

  @Column(name = "refund_amount_mdl", precision = 12, scale = 2)
  @Positive(message = "Сумма возврата в MDL должна быть положительной")
  @Schema(description = "Сумма возврата в молдавских леях", example = "299.90")
  private BigDecimal refundAmountMDL;

  @Column(name = "refund_amount_usd", precision = 12, scale = 2)
  @Positive(message = "Сумма возврата в USD должна быть положительной")
  @Schema(description = "Сумма возврата в долларах США", example = "17.50")
  private BigDecimal refundAmountUSD;

  @Column(length = 1000)
  @Size(max = 1000, message = "Причина возврата не должна превышать 1000 символов")
  @Schema(description = "Причина возврата", example = "Товар не подошел по размеру", requiredMode = Schema.RequiredMode.REQUIRED)
  private String reason;

  @Column(length = 1000)
  @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
  @Schema(description = "Дополнительное описание", example = "Цвет оказался темнее, чем на фото")
  private String description;

  @Column(name = "return_date")
  @Schema(description = "Дата возврата товара", example = "2024-01-20T14:00:00")
  private LocalDateTime returnDate;

  @Column(name = "refund_date")
  @Schema(description = "Дата возврата денег", example = "2024-01-22T10:00:00")
  private LocalDateTime refundDate;

  @Column(name = "return_method", length = 50)
  @Size(max = 50, message = "Способ возврата не должен превышать 50 символов")
  @Schema(description = "Способ возврата", example = "pickup", allowableValues = { "pickup", "delivery", "post" })
  private String returnMethod;

  @Column(name = "tracking_number", length = 100)
  @Size(max = 100, message = "Номер отслеживания не должен превышать 100 символов")
  @Schema(description = "Номер отслеживания возврата", example = "RET123456789")
  private String trackingNumber;

  @JsonManagedReference
  @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Schema(description = "Список товаров для возврата")
  private List<ReturnItem> items;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (status == null) {
      status = ReturnStatus.PENDING;
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
