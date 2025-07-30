package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
