package com.bumbac.order.dto;

import com.bumbac.order.entity.ReturnStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReturnDTO {
    private Long id;
    private Long orderId;
    private ReturnStatus status;
    private BigDecimal refundAmountMdl;
    private BigDecimal refundAmountUsd;
    private LocalDateTime createdAt;
    private List<ReturnItemDTO> items;
}
