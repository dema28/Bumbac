package com.bumbac.order.controller;

import com.bumbac.auth.entity.User;
import com.bumbac.order.entity.Order;
import com.bumbac.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        User user = new User(); // временно, если UserDetails не кастуется
        user.setId(1L);         // заменить на `userService.findByUsername(...)`
        orderService.placeOrder(user);
        return ResponseEntity.ok("Order placed");
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = new User();
        user.setId(1L);
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
