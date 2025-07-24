package com.bumbac.order.mapper;

import com.bumbac.order.dto.PaymentDTO;
import com.bumbac.order.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDto(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .status(p.getStatus().getCode())
                .provider(p.getProvider())
                .providerTxId(p.getProviderTxId())
                .amountMdl(p.getAmountMdl())
                .amountUsd(p.getAmountUsd())
                .paidAt(p.getPaidAt())
                .build();
    }
}
