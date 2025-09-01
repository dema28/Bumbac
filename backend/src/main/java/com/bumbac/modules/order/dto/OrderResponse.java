package com.bumbac.modules.order.dto;

import com.bumbac.shared.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными заказа")
public class OrderResponse {

  @Schema(description = "Уникальный идентификатор заказа", example = "1")
  private Long id;

  @Schema(description = "ID пользователя", example = "1")
  private Long userId;

  @Schema(description = "Email пользователя", example = "user@example.com")
  private String userEmail;

  @Schema(description = "Дата и время создания заказа", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "Дата и время обновления заказа", example = "2024-01-15T10:30:00")
  private LocalDateTime updatedAt;

  @Schema(description = "Общая сумма заказа", example = "299.90")
  private BigDecimal totalAmount;

  @Schema(description = "Статус заказа", example = "NEW")
  private OrderStatus status;

  @Schema(description = "Комментарий к заказу", example = "Доставить до 18:00")
  private String comment;

  @Schema(description = "Предпочитаемая дата доставки", example = "2024-01-20")
  private String preferredDeliveryDate;

  @Schema(description = "Дата доставки", example = "2024-01-20T14:00:00")
  private LocalDateTime deliveredAt;

  @Schema(description = "Список товаров в заказе")
  private List<OrderItemResponse> items;

  @Schema(description = "Количество товаров в заказе", example = "3")
  private Integer itemsCount;
}
