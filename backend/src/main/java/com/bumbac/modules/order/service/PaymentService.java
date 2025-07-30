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

    /**
     * Создает платеж по заказу, который принадлежит переданному пользователю.
     *
     * @param user    пользователь, совершающий платёж
     * @param request тело запроса с параметрами платежа
     * @return созданный платеж
     */
    @Transactional
    public PaymentDTO create(User user, CreatePaymentRequest request) {
        Long userId = user.getId();
        Long orderId = request.getOrderId();
        // Проверка, что пользователь и заказ не null
        System.out.println(">>> AUTH USER ID = " + user.getId());
        System.out.println(">>> REQUESTED ORDER ID = " + orderId);


        // 1. Загружаем заказ с пользователем через JOIN FETCH
        Order order = orderRepository.findWithUserById(orderId, userId)
                .orElseThrow(() -> new AccessDeniedException("Order not found or does not belong to current user"));

        // 2. Получаем статус PENDING
        PaymentStatus status = paymentStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Missing PENDING status"));

        // 3. Проверка ID перед сохранением
        if (order.getId() == null || status.getId() == null) {
            throw new IllegalStateException("Order or PaymentStatus entity has null ID");
        }

        // 4. Логирование
        System.out.println("DEBUG >>> Creating payment for orderId=" + orderId + ", userId=" + userId);

        // 5. Создание платежа
        Payment payment = Payment.builder()
                .order(order)
                .status(status)
                .provider(request.getProvider())
                .providerTxId(request.getProviderTxId())
                .amountMdl(request.getAmountMdl())
                .amountUsd(request.getAmountUsd())
                .paidAt(LocalDateTime.now())
                .build();

        // 6. Сохраняем и возвращаем DTO
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    /**
     * Обрабатывает возврат средств по заказу.
     *
     * @param ret объект возврата с суммой и ID заказа
     * @return созданный возвратный платёж
     */
    @SuppressWarnings("unused")
    public PaymentDTO processRefund(Return ret) {
        // Получаем сущность заказа по ID
        Order order = orderRepository.findById(ret.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Получаем статус REFUNDED
        PaymentStatus refundedStatus = paymentStatusRepository
                .findByCode("REFUNDED")
                .orElseThrow(() -> new ResourceNotFoundException("Missing REFUNDED status"));

        // Создаём платёж на возврат
        Payment refund = Payment.builder()
                .order(order)  // теперь передаём объект Order
                .amountMdl(ret.getRefundAmountMdl())
                .amountUsd(ret.getRefundAmountUsd())
                .status(refundedStatus)
                .provider("REFUND")
                .paidAt(LocalDateTime.now())
                .build();

        return paymentMapper.toDto(paymentRepository.save(refund));
    }
}
