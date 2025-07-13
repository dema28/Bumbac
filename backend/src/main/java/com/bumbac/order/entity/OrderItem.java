package com.bumbac.order.entity;

import com.bumbac.cart.entity.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "color_id")
  private Color color;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Double price;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
}
