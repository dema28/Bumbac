package com.bumbac.modules.order.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.core.exception.ResourceNotFoundException;
import com.bumbac.modules.order.dto.CreatePaymentRequest;
import com.bumbac.modules.order.dto.PaymentDTO;
import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.order.entity.Payment;
import com.bumbac.modules.order.entity.PaymentStatus;
import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.mapper.PaymentMapper;
import com.bumbac.modules.order.repository.OrderRepository;
import com.bumbac.modules.order.repository.PaymentRepository;
import com.bumbac.modules.order.repository.PaymentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    /**
     * Создает платеж по заказу, который принадлежит переданному пользователю.
     */
    @Transactional
    public PaymentDTO create(User user, CreatePaymentRequest request) {
        Long userId = user.getId();
        Long orderId = request.getOrderId();

        // 1) Загружаем заказ с пользователем
        Order order = orderRepository.findWithUserById(orderId, userId)
                .orElseThrow(() -> new AccessDeniedException("Order not found or does not belong to current user"));

        // 2) Статус PENDING
        PaymentStatus status = paymentStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Missing PENDING status"));

        // 3) Создание платежа (ВАЖНО: методы билдера — по именам полей entity)
        Payment payment = Payment.builder()
                .order(order)
                .status(status)
                .provider(request.getProvider())
                .providerTxId(request.getProviderTxId())
                .amountMDL(request.getAmountMdl())   // <-- MDL в верхнем регистре
                .amountUSD(request.getAmountUsd())   // <-- USD в верхнем регистре
                .paidAt(LocalDateTime.now())
                .build();

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    /**
     * Обрабатывает возврат средств по заказу.
     */
    @Transactional
    public PaymentDTO processRefund(Return ret) {
        // Получаем заказ через ссылку из Return
        Order order = orderRepository.findById(
                        ret.getOrder() != null ? ret.getOrder().getId() : null
                )
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        PaymentStatus refundedStatus = paymentStatusRepository.findByCode("REFUNDED")
                .orElseThrow(() -> new ResourceNotFoundException("Missing REFUNDED status"));

        Payment refund = Payment.builder()
                .order(order)
                .status(refundedStatus)
                .provider("REFUND")
                .paidAt(LocalDateTime.now())
                .amountMDL(ret.getRefundAmountMDL())  // <-- точные геттеры из Return
                .amountUSD(ret.getRefundAmountUSD())
                .build();

        return paymentMapper.toDto(paymentRepository.save(refund));
    }
}
