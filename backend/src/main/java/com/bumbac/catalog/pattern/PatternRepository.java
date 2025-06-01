package com.bumbac.catalog.pattern;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatternRepository extends JpaRepository<Pattern, Long> {
    Optional<Pattern> findByCode(String code);
}
