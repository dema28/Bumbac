package com.bumbac.modules.order.mapper;

import com.bumbac.modules.order.dto.ReturnDTO;
import com.bumbac.modules.order.dto.ReturnItemDTO;
import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.entity.ReturnItem;
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
                .refundAmountMdl(r.getRefundAmountMdl())
                .refundAmountUsd(r.getRefundAmountUsd())
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
