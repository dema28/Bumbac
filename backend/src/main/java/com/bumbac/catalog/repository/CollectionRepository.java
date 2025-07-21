package com.bumbac.catalog.repository;

import com.bumbac.catalog.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
