
package com.bumbac.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 👈 ОБЯЗАТЕЛЬНО
    protected Long id;

    @Column(name = "created_at", updatable = false, nullable = false) // 👈 ОБЯЗАТЕЛЬНО
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false) // 👈 ОБЯЗАТЕЛЬНО
    protected LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // ← тоже важно!
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

