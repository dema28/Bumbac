package com.bumbac.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colorId")
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "added_at")
    private LocalDateTime addedAt;
}
