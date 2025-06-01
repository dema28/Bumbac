package com.bumbac.order.repository;

import com.bumbac.order.entity.ReturnStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnStatusHistoryRepository extends JpaRepository<ReturnStatusHistory, Long> {
    List<ReturnStatusHistory> findAllByReturnId(Long returnId);

}
