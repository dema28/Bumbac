package com.bumbac.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long returnId;

    @Enumerated(EnumType.STRING)
    private ReturnStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ReturnStatus newStatus;

    private String changedBy;

    private LocalDateTime changedAt;
}
