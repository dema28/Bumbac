package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.ReturnStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnStatusHistoryRepository extends JpaRepository<ReturnStatusHistory, Long> {
    List<ReturnStatusHistory> findAllByReturnId(Long returnId);

}
