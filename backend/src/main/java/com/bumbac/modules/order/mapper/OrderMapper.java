package com.bumbac.modules.order.mapper;

import com.bumbac.modules.order.dto.OrderResponse;
import com.bumbac.modules.order.dto.OrderItemResponse;
import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

  public OrderResponse toResponse(Order order) {
    if (order == null) {
      return null;
    }

    List<OrderItemResponse> itemResponses = order.getItems() != null ? order.getItems().stream()
        .<OrderItemResponse>map(this::toOrderItemResponse)
        .collect(Collectors.toList()) : null;

    return OrderResponse.builder()
        .id(order.getId())
        .userId(order.getUser() != null ? order.getUser().getId() : null)
        .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .totalAmount(order.getTotalAmount())
        .status(order.getStatus())
        .comment(order.getComment())
        .preferredDeliveryDate(order.getPreferredDeliveryDate())
        .deliveredAt(order.getDeliveredAt())
        .items(itemResponses)
        .itemsCount(itemResponses != null ? itemResponses.size() : 0)
        .build();
  }

  public List<OrderResponse> toResponseList(List<Order> orders) {
    if (orders == null) {
      return null;
    }

    return orders.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private OrderItemResponse toOrderItemResponse(OrderItem item) {
      if (item == null) {
          return null;
      }

      return OrderItemResponse.builder()
              .id(item.getId())
              .colorId(item.getColor() != null ? item.getColor().getId() : null)
              // было: color.getName()
              .colorName(
                      item.getColor() != null && item.getColor().getYarn() != null
                              ? item.getColor().getYarn().getName()
                              : null
              )
              .yarnName(
                      item.getColor() != null && item.getColor().getYarn() != null
                              ? item.getColor().getYarn().getName()
                              : null
              )
              .quantity(item.getQuantity())
              .unitPrice(item.getUnitPrice())
              .totalPrice(item.getTotalPrice())
              // было: color.getImageUrl()
              .imageUrl(null) // TODO: заменить на фактическое поле из Color (например image / imagePath и т.п.
              .build();

  }

}
