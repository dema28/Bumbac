package com.bumbac.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReturnRequestDTO {
    private Long orderId;
    private BigDecimal refundAmountCzk;
    private List<ReturnItemDTO> items;
}
