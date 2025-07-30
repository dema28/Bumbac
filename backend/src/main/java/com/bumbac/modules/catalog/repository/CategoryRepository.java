package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
