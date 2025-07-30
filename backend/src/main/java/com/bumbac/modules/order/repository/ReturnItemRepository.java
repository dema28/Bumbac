package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.ReturnItem;
import com.bumbac.modules.order.entity.ReturnItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnItemRepository extends JpaRepository<ReturnItem, ReturnItemId> {}
