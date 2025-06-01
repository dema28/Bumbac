package com.bumbac.catalog.repository;

import com.bumbac.catalog.entity.Yarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface YarnRepository extends JpaRepository<Yarn, Long>, JpaSpecificationExecutor<Yarn> {
}
