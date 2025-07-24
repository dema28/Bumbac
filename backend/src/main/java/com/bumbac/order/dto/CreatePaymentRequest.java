package com.bumbac.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    private Long orderId;
    private String provider;
    private String providerTxId;
    private BigDecimal amountMdl;
    private BigDecimal amountUsd;
}
