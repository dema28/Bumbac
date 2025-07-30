package com.bumbac.modules.pattern.entity;

import com.bumbac.modules.catalog.entity.Yarn;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "patterns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @JoinColumn(name = "yarn_id")
    private Yarn yarn;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "pattern", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatternTranslation> translations;
}
