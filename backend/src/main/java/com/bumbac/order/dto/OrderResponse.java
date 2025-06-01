package com.bumbac.order.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private List<OrderItemResponse> items;
}
