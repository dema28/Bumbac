package com.bumbac.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private PaymentStatus status;

    private String provider;
    private String providerTxId;

    @Column(precision = 12, scale = 2)
    private BigDecimal amountMdl;

    @Column(precision = 12, scale = 2)
    private BigDecimal amountUsd;

    private LocalDateTime paidAt;
}
