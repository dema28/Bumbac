package com.bumbac.order.service;

import com.bumbac.order.dto.CreatePaymentRequest;
import com.bumbac.order.dto.PaymentDTO;
import com.bumbac.order.entity.Payment;
import com.bumbac.order.entity.PaymentStatus;
import com.bumbac.order.entity.Return;
import com.bumbac.order.mapper.PaymentMapper;
import com.bumbac.order.repository.PaymentRepository;
import com.bumbac.order.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }
    public List<PaymentDTO> getByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public PaymentDTO create(CreatePaymentRequest request) {
        PaymentStatus status = paymentStatusRepository
                .findByCode("PENDING")
                .orElseThrow(() -> new RuntimeException("Missing PENDING status"));

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .provider(request.getProvider())
                .providerTxId(request.getProviderTxId())
                .amountMdl(request.getAmountMdl())
                .amountUsd(request.getAmountUsd())

                .status(status)
                .paidAt(LocalDateTime.now())
                .build();

        return paymentMapper.toDto(paymentRepository.save(payment));
    }
    public PaymentDTO processRefund(Return ret) {
        PaymentStatus refundedStatus = paymentStatusRepository
                .findByCode("REFUNDED")
                .orElseThrow(() -> new RuntimeException("Missing REFUNDED status"));

        Payment refund = Payment.builder()
                .orderId(ret.getOrderId())
                .amountMdl(ret.getRefundAmountMdl())
                .amountUsd(ret.getRefundAmountUsd())

                .status(refundedStatus)
                .provider("REFUND")
                .paidAt(LocalDateTime.now())
                .build();

        return paymentMapper.toDto(paymentRepository.save(refund));
    }

}
