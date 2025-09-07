package com.bumbac.modules.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class YarnAttributeValuesId implements Serializable {

    @Column(name = "yarn_id", nullable = false)
    private Long yarnId;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;
}