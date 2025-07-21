package com.bumbac.catalog.repository;

import com.bumbac.catalog.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
