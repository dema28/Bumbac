package com.bumbac.order.dto;

import lombok.Data;

@Data
public class ReturnItemDTO {
    private Long colorId;
    private int quantity;
    private String reason;
}
