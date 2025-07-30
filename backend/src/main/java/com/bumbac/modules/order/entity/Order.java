package com.bumbac.modules.order.entity;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.shared.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @JsonManagedReference
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  private LocalDateTime deliveredAt;

}
