package com.bumbac.cart.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemId implements Serializable {
    private Long cartId;
    private Long colorId;
}
