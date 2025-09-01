package com.bumbac.modules.pattern.repository;

import com.bumbac.modules.pattern.entity.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatternRepository extends JpaRepository<Pattern, Long> {
    boolean existsByCode(String code);
    Optional<Pattern> findByCode(String code);
}
