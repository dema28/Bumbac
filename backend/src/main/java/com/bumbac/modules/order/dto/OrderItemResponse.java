package com.bumbac.modules.order.dto;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long yarnId;
    private Integer quantity;
    private Double price;
}
