package com.bumbac.modules.order.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.repository.CartItemRepository;
import com.bumbac.modules.cart.repository.CartRepository;
import com.bumbac.modules.order.dto.ReturnDTO;
import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.order.entity.OrderItem;
import com.bumbac.shared.enums.OrderStatus;
import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.repository.OrderRepository;
import com.bumbac.modules.order.repository.ReturnRepository;
import com.bumbac.modules.order.mapper.ReturnMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Order placeOrder(User user) {
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
                .status(OrderStatus.NEW)
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
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByCart(cart);
        return savedOrder;
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
