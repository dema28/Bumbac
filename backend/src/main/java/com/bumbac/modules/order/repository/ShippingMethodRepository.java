package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {}
