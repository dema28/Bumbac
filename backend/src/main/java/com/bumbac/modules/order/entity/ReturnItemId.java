package com.bumbac.modules.order.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemId implements Serializable {
    private Long returnId;
    private Long colorId;
}
