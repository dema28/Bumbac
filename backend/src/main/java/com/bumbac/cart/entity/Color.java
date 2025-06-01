package com.bumbac.cart.entity;

import com.bumbac.catalog.entity.Yarn;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String colorCode;
    private String colorName;
    private String sku;
    private String barcode;
    private String hexValue;
    private Integer stockQuantity;

    @Column(nullable = false)
    private BigDecimal price;  // 💰 Обязательно для OrderService

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yarn_id")
    private Yarn yarn;
}
