package com.bumbac.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String status;
    private String provider;
    private String providerTxId;
    private BigDecimal amountCzk;
    private LocalDateTime paidAt;
}
