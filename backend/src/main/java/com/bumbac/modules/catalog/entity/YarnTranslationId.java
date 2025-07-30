package com.bumbac.modules.catalog.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YarnTranslationId implements Serializable {
    private Long yarnId;
    private String locale;
}
