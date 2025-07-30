package com.bumbac.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String carrier;

    private Double price;

    private String estimatedTime;
}
