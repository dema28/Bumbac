package com.bumbac.order.repository;

import com.bumbac.order.entity.Order;
import com.bumbac.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
