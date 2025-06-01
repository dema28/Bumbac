package com.bumbac.order.service;

import com.bumbac.cart.repository.ColorRepository;
import com.bumbac.order.dto.*;
import com.bumbac.order.entity.*;
import com.bumbac.order.mapper.ReturnMapper;
import com.bumbac.order.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;
    private final ColorRepository colorRepository;
    private final OrderRepository orderRepository;
    private final ReturnItemRepository returnItemRepository;
    private final PaymentService paymentService;
    private final ReturnStatusHistoryRepository returnStatusHistoryRepository;


    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    public List<ReturnDTO> getAllReturnDTOs() {
        return returnRepository.findAll()
                .stream()
                .map(returnMapper::toDto)
                .toList();
    }

    public ReturnDTO createReturnDTO(ReturnRequestDTO dto, String userEmail) {
        // 1. Валидация
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Order does not belong to user");
        }

        if (!OrderStatus.DELIVERED.equals(order.getStatus())) {
            throw new IllegalStateException("Only delivered orders can be returned");
        }

        if (order.getDeliveredAt() != null && order.getDeliveredAt().isBefore(LocalDateTime.now().minusDays(14))) {
            throw new IllegalStateException("Return window (14 days) has expired");
        }

        // 2. Создание возврата
        Return ret = new Return();
        ret.setOrderId(order.getId());
        ret.setStatus(ReturnStatus.REQUESTED);
        ret.setCreatedAt(LocalDateTime.now());

        // 3. Сохраняем возврат, получаем ID
        returnRepository.save(ret);

        // 4. Подсчёт суммы возврата
        AtomicReference<BigDecimal> totalRefund = new AtomicReference<>(BigDecimal.ZERO);

        // 5. Создание возвратов для каждого элемента
        List<ReturnItem> returnItems = dto.getItems().stream().map(itemDTO -> {
            OrderItem matchedItem = order.getItems().stream()
                    .filter(oi -> oi.getColor().getId().equals(itemDTO.getColorId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Color not found in order"));

            if (itemDTO.getQuantity() > matchedItem.getQuantity()) {
                throw new IllegalArgumentException("Return quantity exceeds ordered quantity");
            }

            BigDecimal itemRefund = BigDecimal.valueOf(matchedItem.getPrice())
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalRefund.updateAndGet(v -> v.add(itemRefund)); // обновляем итоговую сумму

            ReturnItem item = new ReturnItem();
            item.setId(new ReturnItemId(ret.getId(), itemDTO.getColorId())); // связь с возвратом и цветом
            item.setReturnEntity(ret);
            item.setColor(colorRepository.findById(itemDTO.getColorId())
                    .orElseThrow(() -> new RuntimeException("Color not found"))); // находим цвет
            item.setQuantity(itemDTO.getQuantity());
            item.setReason(itemDTO.getReason());
            return item;
        }).toList();

        // Сохраняем все возвраты
        returnItemRepository.saveAll(returnItems);

        // 6. Итоговая сумма и обновление возврата
        ret.setRefundAmountCzk(totalRefund.get()); // обновляем сумму возврата
        ret.setUpdatedAt(LocalDateTime.now());
        returnRepository.save(ret); // сохраняем обновлённый возврат

        // 7. Обновление статуса заказа
        order.setStatus(OrderStatus.RETURNED);
        orderRepository.save(order);

        // 8. Возвращаем DTO для возврата
        return returnMapper.toDto(ret);
    }


    public List<ReturnDTO> getReturnsByStatus(ReturnStatus status) {
        List<Return> list = (status == null)
                ? returnRepository.findAll()
                : returnRepository.findByStatus(status);
        return list.stream().map(returnMapper::toDto).toList();
    }

    public ReturnDTO updateReturnStatus(Long id, ReturnStatus status) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));

        ReturnStatus oldStatus = ret.getStatus(); // сохраняем старый статус
        ret.setStatus(status);
        ret.setUpdatedAt(LocalDateTime.now());

        Return saved = returnRepository.save(ret);

        // ✅ Запись истории
        ReturnStatusHistory history = ReturnStatusHistory.builder()
                .returnId(saved.getId())
                .oldStatus(oldStatus)
                .newStatus(status)
                .changedBy("admin@bumbac.md") // TODO: заменить на текущего пользователя
                .changedAt(LocalDateTime.now())
                .build();
        returnStatusHistoryRepository.save(history);

        // 💰 Возврат средств, если статус REFUNDED
        if (status == ReturnStatus.REFUNDED) {
            paymentService.processRefund(saved);
        }

        return returnMapper.toDto(saved);
    }

    public List<ReturnStatusHistory> getHistoryByReturnId(Long returnId) {
        return returnStatusHistoryRepository.findAllByReturnId(returnId);
    }
    public ReturnDTO getReturnById(Long id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
        return returnMapper.toDto(ret);
    }



}
