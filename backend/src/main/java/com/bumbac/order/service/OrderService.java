package com.bumbac.order.service;

import com.bumbac.auth.entity.User;
import com.bumbac.cart.entity.Cart;
import com.bumbac.cart.entity.CartItem;
import com.bumbac.cart.repository.CartItemRepository;
import com.bumbac.cart.repository.CartRepository;
import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.entity.Order;
import com.bumbac.order.entity.OrderItem;
import com.bumbac.order.entity.Return;
import com.bumbac.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bumbac.order.repository.ReturnRepository;
import com.bumbac.order.mapper.ReturnMapper;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;


    @Transactional
    public void placeOrder(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal total = cartItems.stream()
                .map(item -> item.getColor().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .totalAmount(total)
                .build();

        List<OrderItem> items = cartItems.stream()
                .map(item -> OrderItem.builder()
                        .order(order)
                        .color(item.getColor())
                        .quantity(item.getQuantity())
                        .price(item.getColor().getPrice().doubleValue())
                        .build())
                .collect(Collectors.toList());

        order.setItems(items);
        orderRepository.save(order);

        cartItemRepository.deleteByCart(cart);
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public ReturnDTO getReturnById(Long id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
        return returnMapper.toDto(ret);
    }

}
