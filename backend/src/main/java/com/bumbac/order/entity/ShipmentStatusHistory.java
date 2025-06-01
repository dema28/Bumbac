package com.bumbac.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;

    private String status;

    private LocalDateTime changedAt;

    private String changedBy;
}
