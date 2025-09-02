package com.bumbac.modules.order.service;

import com.bumbac.modules.cart.repository.ColorRepository;
import com.bumbac.modules.order.dto.ReturnDTO;
import com.bumbac.modules.order.dto.ReturnRequestDTO;
import com.bumbac.modules.order.dto.ReturnItemDTO;
import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.order.entity.OrderItem;
import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.entity.ReturnItem;
import com.bumbac.modules.order.entity.ReturnItemId;
import com.bumbac.modules.order.entity.ReturnStatusHistory;
import com.bumbac.modules.order.mapper.ReturnMapper;
import com.bumbac.modules.order.repository.OrderRepository;
import com.bumbac.modules.order.repository.ReturnItemRepository;
import com.bumbac.modules.order.repository.ReturnRepository;
import com.bumbac.modules.order.repository.ReturnStatusHistoryRepository;
import com.bumbac.shared.enums.OrderStatus;
import com.bumbac.shared.enums.ReturnStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ReturnDTO> getAllReturnDTOs() {
        return returnRepository.findAll()
                .stream()
                .map(returnMapper::toDto)
                .toList();
    }

    @Transactional
    public ReturnDTO createReturnDTO(ReturnRequestDTO dto, String userEmail) {
        // 1) Валидация заказа и окна возврата
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Order does not belong to user");
        }
        if (!OrderStatus.DELIVERED.equals(order.getStatus())) {
            throw new IllegalStateException("Only delivered orders can be returned");
        }
        if (order.getDeliveredAt() != null &&
                order.getDeliveredAt().isBefore(LocalDateTime.now().minusDays(14))) {
            throw new IllegalStateException("Return window (14 days) has expired");
        }

        // 2) Создание возврата
        Return ret = new Return();
        ret.setOrder(order);
        ret.setStatus(ReturnStatus.PENDING);
        ret.setCreatedAt(LocalDateTime.now());

        // Сохраняем возврат и получаем ID
        Return savedReturn = returnRepository.save(ret);
        final Long returnId = savedReturn.getId(); // делаем final для использования в цикле

        // 3) Подсчёт суммы возврата (используем обычный цикл вместо lambda)
        BigDecimal totalRefund = BigDecimal.ZERO;
        List<ReturnItem> returnItems = new ArrayList<>();

        // 4) Создание позиций возврата
        for (ReturnItemDTO itemDTO : dto.getItems()) {
            OrderItem matchedItem = order.getItems().stream()
                    .filter(oi -> oi.getColor().getId().equals(itemDTO.getColorId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Color not found in order"));

            if (itemDTO.getQuantity() > matchedItem.getQuantity()) {
                throw new IllegalArgumentException("Return quantity exceeds ordered quantity");
            }

            // Используем unitPrice для расчета суммы возврата
            BigDecimal itemRefund = matchedItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalRefund = totalRefund.add(itemRefund);

            ReturnItem item = new ReturnItem();
            item.setId(new ReturnItemId(returnId, itemDTO.getColorId()));
            item.setReturnEntity(savedReturn);
            item.setColor(colorRepository.findById(itemDTO.getColorId())
                    .orElseThrow(() -> new RuntimeException("Color not found")));
            item.setQuantity(itemDTO.getQuantity());
            item.setReason(itemDTO.getReason());
            returnItems.add(item);
        }

        // Сохраняем все позиции возврата
        returnItemRepository.saveAll(returnItems);

        // 5) Итоговые суммы возврата (MDL/USD)
        savedReturn.setRefundAmountMDL(totalRefund);
        savedReturn.setRefundAmountUSD(totalRefund); // TODO: при необходимости конвертировать
        savedReturn.setUpdatedAt(LocalDateTime.now());
        savedReturn = returnRepository.save(savedReturn);

        // 6) Обновление статуса заказа
        order.setStatus(OrderStatus.RETURNED);
        orderRepository.save(order);

        // 7) Возвращаем DTO
        return returnMapper.toDto(savedReturn);
    }

    @Transactional(readOnly = true)
    public List<ReturnDTO> getReturnsByStatus(ReturnStatus status) {
        List<Return> list = (status == null)
                ? returnRepository.findAll()
                : returnRepository.findByStatus(status);
        return list.stream().map(returnMapper::toDto).toList();
    }

    @Transactional
    public ReturnDTO updateReturnStatus(Long id, ReturnStatus status) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));

        ReturnStatus oldStatus = ret.getStatus();
        ret.setStatus(status);
        ret.setUpdatedAt(LocalDateTime.now());
        Return saved = returnRepository.save(ret);

        // история статусов
        ReturnStatusHistory history = ReturnStatusHistory.builder()
                .returnId(saved.getId())
                .oldStatus(oldStatus)
                .newStatus(status)
                .changedBy("admin@bumbac.md") // TODO: заменить на текущего пользователя
                .changedAt(LocalDateTime.now())
                .build();
        returnStatusHistoryRepository.save(history);

        // возврат средств
        if (status == ReturnStatus.REFUNDED) {
            paymentService.processRefund(saved);
        }

        return returnMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReturnStatusHistory> getHistoryByReturnId(Long returnId) {
        return returnStatusHistoryRepository.findAllByReturnId(returnId);
    }

    @Transactional(readOnly = true)
    public ReturnDTO getReturnById(Long id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
        return returnMapper.toDto(ret);
    }
}