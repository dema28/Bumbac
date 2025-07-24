package com.bumbac.cart.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  private java.time.LocalDateTime createdAt;
  private java.time.LocalDateTime updatedAt;

  @JsonManagedReference
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items;
}
