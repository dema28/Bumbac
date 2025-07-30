package com.bumbac.modules.cart.entity;

import com.bumbac.modules.user.entity.CartItemId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
public class CartItem {

  @EmbeddedId
  private CartItemId id;

  @JsonBackReference
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

  // ✳️ Конструктор без использования @Builder
  public CartItem(CartItemId id, Cart cart, Color color, int quantity, LocalDateTime addedAt) {
    this.id = id;
    this.cart = cart;
    this.color = color;
    this.quantity = quantity;
    this.addedAt = addedAt;
  }
}
