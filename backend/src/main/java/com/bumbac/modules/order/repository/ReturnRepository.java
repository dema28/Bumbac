package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.entity.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findByStatus(ReturnStatus status);
}
