package com.bumbac.order.mapper;

import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.dto.ReturnItemDTO;
import com.bumbac.order.entity.Return;
import com.bumbac.order.entity.ReturnItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReturnMapper {

    public ReturnDTO toDto(Return r) {
        List<ReturnItemDTO> items = r.getItems().stream()
                .map(this::toItemDto)
                .toList();

        return ReturnDTO.builder()
                .id(r.getId())
                .orderId(r.getOrderId())
                .status(r.getStatus())
                .refundAmountCzk(r.getRefundAmountCzk())
                .createdAt(r.getCreatedAt())
                .items(items)
                .build();
    }

    public ReturnItemDTO toItemDto(ReturnItem item) {
        ReturnItemDTO dto = new ReturnItemDTO();
        dto.setColorId(item.getColor().getId());
        dto.setQuantity(item.getQuantity());
        dto.setReason(item.getReason());
        return dto;
    }
}
