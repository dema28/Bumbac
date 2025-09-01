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
        if (r == null) return null;

        List<ReturnItemDTO> items = (r.getItems() == null ? List.of() :
                r.getItems().stream().map(this::toItemDto).toList());

        return ReturnDTO.builder()
                .id(r.getId())
                .orderId(r.getOrder() != null ? r.getOrder().getId() : null)
                .status(r.getStatus())
                .refundAmountMdl(r.getRefundAmountMDL())
                .refundAmountUsd(r.getRefundAmountUSD())
                .createdAt(r.getCreatedAt())
                .items(items)
                .build();
    }

    public ReturnItemDTO toItemDto(ReturnItem item) {
        if (item == null) return null;
        ReturnItemDTO dto = new ReturnItemDTO();
        dto.setColorId(item.getColor() != null ? item.getColor().getId() : null);
        dto.setQuantity(item.getQuantity());
        dto.setReason(item.getReason());
        return dto;
    }

}
