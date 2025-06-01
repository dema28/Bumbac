package com.bumbac.cart.dto;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long colorId;
    private int quantity;
}
