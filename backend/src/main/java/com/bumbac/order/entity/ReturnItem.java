package com.bumbac.order.entity;

import com.bumbac.cart.entity.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "return_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnItem {

  @EmbeddedId
  private ReturnItemId id;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("returnId")
  @JoinColumn(name = "return_id")
  private Return returnEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("colorId")
  @JoinColumn(name = "color_id")
  private Color color;

  @Column(nullable = false)
  private int quantity;

  private String reason;
}
