package com.bumbac.cart.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long colorId;
    private int quantity;
}
