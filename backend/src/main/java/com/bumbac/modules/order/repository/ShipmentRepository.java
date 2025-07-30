package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {}
