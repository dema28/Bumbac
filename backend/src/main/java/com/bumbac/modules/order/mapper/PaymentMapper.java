package com.bumbac.modules.order.mapper;

import com.bumbac.modules.order.dto.PaymentDTO;
import com.bumbac.modules.order.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDto(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .status(p.getStatus().getCode())
                .provider(p.getProvider())
                .providerTxId(p.getProviderTxId())
                .amountMdl(p.getAmountMDL())
                .amountUsd(p.getAmountUSD())
                .paidAt(p.getPaidAt())
                .build();
    }
}