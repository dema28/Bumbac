package com.bumbac.order.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long orderId;

  @Enumerated(EnumType.STRING)
  private ReturnStatus status;

  @Column(precision = 12, scale = 2)
  private BigDecimal refundAmountMdl;

  @Column(precision = 12, scale = 2)
  private BigDecimal refundAmountUsd;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @JsonManagedReference
  @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL)
  private List<ReturnItem> items;
}
