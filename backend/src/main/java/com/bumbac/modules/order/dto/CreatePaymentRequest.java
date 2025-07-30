package com.bumbac.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    @NotNull
    private Long orderId;
    private String provider;
    private String providerTxId;
    private BigDecimal amountMdl;
    private BigDecimal amountUsd;
}
