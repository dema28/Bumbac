package com.bumbac.order.service;

import com.bumbac.cart.repository.ColorRepository;
import com.bumbac.order.dto.*;
import com.bumbac.order.entity.*;
import com.bumbac.order.mapper.ReturnMapper;
import com.bumbac.order.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;
    private final ColorRepository colorRepository; // 🔧 Вставлено!

    public List<Return> getAllReturns() {
        return returnRepository.findAll();
    }

    public List<ReturnDTO> getAllReturnDTOs() {
        return returnRepository.findAll()
                .stream()
                .map(returnMapper::toDto)
                .toList();
    }

    public ReturnDTO createReturnDTO(ReturnRequestDTO dto) {
        Return created = createReturn(dto);
        return returnMapper.toDto(created);
    }

    public Return createReturn(ReturnRequestDTO dto) {
        // 1. Создаём Return без ID
        Return returnEntity = Return.builder()
                .orderId(dto.getOrderId())
                .refundAmountCzk(dto.getRefundAmountCzk())
                .status(ReturnStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 2. Сохраняем Return, чтобы получить ID
        Return saved = returnRepository.save(returnEntity);

        // 3. Создаём ReturnItem со связанным Color и id
        List<ReturnItem> items = dto.getItems().stream()
                .map(i -> ReturnItem.builder()
                        .id(new ReturnItemId(saved.getId(), i.getColorId()))
                        .color(colorRepository.getReferenceById(i.getColorId()))
                        .quantity(i.getQuantity())
                        .reason(i.getReason())
                        .returnEntity(saved)
                        .build())
                .toList();

        // 4. Привязываем и сохраняем повторно
        saved.setItems(items);
        return returnRepository.save(saved);
    }
}
