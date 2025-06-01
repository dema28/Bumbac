package com.bumbac.order.repository;

import com.bumbac.order.entity.ReturnItem;
import com.bumbac.order.entity.ReturnItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, ReturnItemId> {}
