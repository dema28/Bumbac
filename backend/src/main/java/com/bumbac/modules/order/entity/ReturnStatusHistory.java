package com.bumbac.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.bumbac.shared.enums.ReturnStatus;

@Entity
@Table(name = "return_status_history")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReturnStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "return_id", nullable = false)
    private Long returnId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false)
    private ReturnStatus oldStatus;   // shared

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ReturnStatus newStatus;   // shared

    @Column(name = "changed_by", length = 255)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private java.time.LocalDateTime changedAt;
}
