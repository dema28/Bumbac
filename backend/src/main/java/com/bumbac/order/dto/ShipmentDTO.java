package com.bumbac.order.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private String shippingMethodName;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
