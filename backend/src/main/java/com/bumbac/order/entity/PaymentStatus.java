package com.bumbac.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // ENUM-like: PENDING, SUCCESS, etc.

    private String name;
}
