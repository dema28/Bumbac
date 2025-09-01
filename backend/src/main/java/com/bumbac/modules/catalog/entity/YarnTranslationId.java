package com.bumbac.modules.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class YarnTranslationId implements Serializable {

    @Column(name = "yarn_id", nullable = false)
    private Long yarnId;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale;
}
