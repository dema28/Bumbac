package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
