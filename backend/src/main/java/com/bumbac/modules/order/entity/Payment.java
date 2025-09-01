package com.bumbac.modules.order.entity;

import com.bumbac.modules.order.entity.Order;
import jakarta.validation.constraints.NotBlank;
import com.bumbac.shared.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_order_id", columnList = "order_id"),
    @Index(name = "idx_payment_status_id", columnList = "status_id"),
    @Index(name = "idx_payment_provider", columnList = "provider"),
    @Index(name = "idx_payment_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Платеж по заказу")
public class Payment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  @JsonBackReference
  @NotNull(message = "Заказ обязателен")
  @Schema(description = "Заказ, к которому относится платеж", requiredMode = Schema.RequiredMode.REQUIRED)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "status_id", nullable = false)
  @NotNull(message = "Статус платежа обязателен")
  @Schema(description = "Статус платежа", requiredMode = Schema.RequiredMode.REQUIRED)
  private PaymentStatus status;

  @Column(nullable = false, length = 100)
  @NotBlank(message = "Провайдер платежа обязателен")
  @Size(max = 100, message = "Название провайдера не должно превышать 100 символов")
  @Schema(description = "Провайдер платежа", example = "Visa", requiredMode = Schema.RequiredMode.REQUIRED)
  private String provider;

  @Column(name = "provider_tx_id", length = 255)
  @Size(max = 255, message = "ID транзакции провайдера не должен превышать 255 символов")
  @Schema(description = "ID транзакции провайдера", example = "tx_123456789")
  private String providerTxId;

  @Column(name = "amount_mdl", nullable = false, precision = 12, scale = 2)
  @NotNull(message = "Сумма в MDL обязательна")
  @Positive(message = "Сумма в MDL должна быть положительной")
  @Schema(description = "Сумма платежа в молдавских леях", example = "299.90", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal amountMDL;

  @Column(name = "amount_usd", precision = 12, scale = 2)
  @Positive(message = "Сумма в USD должна быть положительной")
  @Schema(description = "Сумма платежа в долларах США", example = "17.50")
  private BigDecimal amountUSD;

  @Column(name = "paid_at")
  @Schema(description = "Дата и время оплаты", example = "2024-01-15T10:30:00")
  private LocalDateTime paidAt;

  @Column(name = "payment_method", length = 50)
  @Size(max = 50, message = "Способ оплаты не должен превышать 50 символов")
  @Schema(description = "Способ оплаты", example = "card", allowableValues = { "card", "cash", "transfer" })
  private String paymentMethod;

  @Column(name = "currency", length = 3)
  @Size(max = 3, message = "Код валюты не должен превышать 3 символа")
  @Schema(description = "Код валюты", example = "MDL", allowableValues = { "MDL", "USD", "EUR" })
  private String currency;

  @Column(name = "exchange_rate", precision = 10, scale = 6)
  @Positive(message = "Курс обмена должен быть положительным")
  @Schema(description = "Курс обмена валюты", example = "17.123456")
  private BigDecimal exchangeRate;

  @Column(length = 1000)
  @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
  @Schema(description = "Описание платежа", example = "Оплата заказа #123")
  private String description;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
    if (currency == null) {
      currency = "MDL";
    }
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
