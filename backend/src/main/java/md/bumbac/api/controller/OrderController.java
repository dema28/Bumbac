package md.bumbac.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Order;
import md.bumbac.api.security.UserDetailsImpl;
import md.bumbac.api.service.OrderService;
import md.bumbac.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Работа с заказами:
 * • пользователь — создание и просмотр своих заказов;
 * • администратор — просмотр всех заказов.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final UserService  userService;

    /* ---------- заказы текущего пользователя --------------------------- */

    @GetMapping("/me")
    public List<Order> myOrders(@AuthenticationPrincipal UserDetailsImpl principal) {
        var user = userService.findByEmail(principal.getEmail()).orElseThrow();
        return orderService.getOrdersByUser(user);
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal UserDetailsImpl principal,
                                            @Valid @RequestBody Order order) {

        var user   = userService.findByEmail(principal.getEmail()).orElseThrow();
        var saved  = orderService.createOrder(order, user);

        return ResponseEntity
                .created(URI.create("/api/orders/" + saved.getId()))
                .body(saved);
    }

    /* ---------- админ-эндпойнт: все заказы ------------------------------ */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CONTENT_MANAGER')")
    public List<Order> allOrders() {
        // method getOrdersByUser(null) может быть реализован как findAll()
        // чтобы не менять сервис, просто возвращаем findAll() напрямую:
        return orderService.findAll();   // добавьте findAll() в OrderService / Repository
    }
}
