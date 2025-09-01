package com.bumbac.modules.order.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.repository.CartItemRepository;
import com.bumbac.modules.cart.repository.CartRepository;
import com.bumbac.modules.order.dto.CreateOrderRequest;
import com.bumbac.modules.order.dto.OrderResponse;
import com.bumbac.modules.order.dto.ReturnDTO;
import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.order.entity.OrderItem;
import com.bumbac.modules.order.entity.Return;
import com.bumbac.modules.order.mapper.OrderMapper;
import com.bumbac.modules.order.mapper.ReturnMapper;
import com.bumbac.modules.order.repository.OrderRepository;
import com.bumbac.modules.order.repository.ReturnRepository;
import com.bumbac.shared.enums.OrderStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ReturnRepository returnRepository;
    private final ReturnMapper returnMapper;
    private final OrderMapper orderMapper;
    private final MeterRegistry meterRegistry;

    // Метрики
    private Counter orderCreatedCounter;
    private Counter orderViewedCounter;
    private Counter orderStatusChangedCounter;
    private Counter orderErrorCounter;

    @PostConstruct
    void initMetrics() {
        this.orderCreatedCounter = Counter.builder("order.operations.created")
                .description("Количество созданных заказов")
                .register(meterRegistry);
        this.orderViewedCounter = Counter.builder("order.operations.viewed")
                .description("Количество просмотров заказов")
                .register(meterRegistry);
        this.orderStatusChangedCounter = Counter.builder("order.operations.status_changed")
                .description("Количество изменений статуса заказов")
                .register(meterRegistry);
        this.orderErrorCounter = Counter.builder("order.operations.errors")
                .description("Количество ошибок при операциях с заказами")
                .register(meterRegistry);
    }

    @Transactional
    public OrderResponse placeOrder(User user, CreateOrderRequest request) {
        log.info("Создание нового заказа для пользователя: {}", user.getEmail());
        try {
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> {
                        log.warn("Корзина не найдена для пользователя: {}", user.getEmail());
                        orderErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Корзина не найдена");
                    });

            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            if (cartItems.isEmpty()) {
                log.warn("Корзина пуста для пользователя: {}", user.getEmail());
                orderErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Корзина пуста");
            }

            BigDecimal total = cartItems.stream()
                    .map(item -> item.getColor().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = Order.builder()
                    .user(user)
                    .totalAmount(total)
                    .status(OrderStatus.NEW)
                    .comment(request.getComment())
                    .preferredDeliveryDate(request.getPreferredDeliveryDate())
                    .build();

            List<OrderItem> items = cartItems.stream()
                    .map(item -> OrderItem.builder()
                            .order(order)
                            .color(item.getColor())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getColor().getPrice())
                            .totalPrice(item.getColor().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build())
                    .collect(Collectors.toList());

            order.setItems(items);
            Order savedOrder = orderRepository.save(order);

            cartItemRepository.deleteByCart(cart);
            log.debug("Корзина очищена для пользователя: {}", user.getEmail());

            OrderResponse response = orderMapper.toResponse(savedOrder);

            log.info("Заказ успешно создан: id={}, пользователь={}, сумма={}",
                    savedOrder.getId(), user.getEmail(), total);
            orderCreatedCounter.increment();

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при создании заказа для пользователя {}: {}", user.getEmail(), e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при создании заказа");
        }
    }

    @Transactional
    public OrderResponse placeOrderFromCart(User user) {
        log.info("Создание заказа из корзины для пользователя: {}", user.getEmail());
        try {
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> {
                        log.warn("Корзина не найдена для пользователя: {}", user.getEmail());
                        orderErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Корзина не найдена");
                    });

            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            if (cartItems.isEmpty()) {
                log.warn("Корзина пуста для пользователя: {}", user.getEmail());
                orderErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Корзина пуста");
            }

            BigDecimal total = cartItems.stream()
                    .map(item -> item.getColor().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Order order = Order.builder()
                    .user(user)
                    .totalAmount(total)
                    .status(OrderStatus.NEW)
                    .build();

            List<OrderItem> items = cartItems.stream()
                    .map(item -> OrderItem.builder()
                            .order(order)
                            .color(item.getColor())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getColor().getPrice())
                            .totalPrice(item.getColor().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build())
                    .collect(Collectors.toList());

            order.setItems(items);
            Order savedOrder = orderRepository.save(order);

            cartItemRepository.deleteByCart(cart);
            log.debug("Корзина очищена для пользователя: {}", user.getEmail());

            OrderResponse response = orderMapper.toResponse(savedOrder);

            log.info("Заказ успешно создан из корзины: id={}, пользователь={}, сумма={}",
                    savedOrder.getId(), user.getEmail(), total);
            orderCreatedCounter.increment();

            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при создании заказа из корзины для пользователя {}: {}", user.getEmail(), e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при создании заказа");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(User user) {
        log.debug("Получение заказов пользователя: {}", user.getEmail());
        try {
            List<Order> orders = orderRepository.findByUser(user);
            List<OrderResponse> responses = orders.stream()
                    .map(orderMapper::toResponse)
                    .collect(Collectors.toList());

            log.debug("Получено {} заказов для пользователя: {}", responses.size(), user.getEmail());
            orderViewedCounter.increment();
            return responses;

        } catch (Exception e) {
            log.error("Ошибка при получении заказов пользователя {}: {}", user.getEmail(), e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении заказов");
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, User user) {
        log.debug("Получение заказа по ID: {} для пользователя: {}", orderId, user.getEmail());
        try {
            Order order = orderRepository.findWithUserById(orderId, user.getId())
                    .orElseThrow(() -> {
                        log.warn("Заказ не найден: id={}, пользователь={}", orderId, user.getEmail());
                        orderErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден");
                    });

            OrderResponse response = orderMapper.toResponse(order);

            log.debug("Заказ найден: id={}, пользователь={}", orderId, user.getEmail());
            orderViewedCounter.increment();
            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении заказа {} для пользователя {}: {}", orderId, user.getEmail(), e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении заказа");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Получение всех заказов");
        try {
            List<Order> orders = orderRepository.findAllWithUser();
            List<OrderResponse> responses = orders.stream()
                    .map(orderMapper::toResponse)
                    .collect(Collectors.toList());

            log.debug("Получено {} заказов", responses.size());
            orderViewedCounter.increment();
            return responses;

        } catch (Exception e) {
            log.error("Ошибка при получении всех заказов: {}", e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении заказов");
        }
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Обновление статуса заказа: id={}, новый статус={}", orderId, newStatus);
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.warn("Заказ не найден для обновления статуса: id={}", orderId);
                        orderErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден");
                    });

            OrderStatus oldStatus = order.getStatus();
            order.setStatus(newStatus);

            if (newStatus == OrderStatus.DELIVERED) {
                order.setDeliveredAt(LocalDateTime.now());
            }

            Order updatedOrder = orderRepository.save(order);
            OrderResponse response = orderMapper.toResponse(updatedOrder);

            log.info("Статус заказа обновлен: id={}, старый статус={}, новый статус={}",
                    orderId, oldStatus, newStatus);
            orderStatusChangedCounter.increment();
            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении статуса заказа {}: {}", orderId, e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении статуса заказа");
        }
    }

    @Transactional(readOnly = true)
    public ReturnDTO getReturnById(Long id) {
        log.debug("Получение возврата по ID: {}", id);
        try {
            Return ret = returnRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Возврат не найден: id={}", id);
                        orderErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Возврат не найден");
                    });

            ReturnDTO response = returnMapper.toDto(ret);
            log.debug("Возврат найден: id={}", id);
            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении возврата {}: {}", id, e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении возврата");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        log.debug("Получение заказов по статусу: {}", status);
        try {
            List<Order> orders = orderRepository.findByStatus(status);
            List<OrderResponse> responses = orders.stream()
                    .map(orderMapper::toResponse)
                    .collect(Collectors.toList());

            log.debug("Получено {} заказов со статусом: {}", responses.size(), status);
            orderViewedCounter.increment();
            return responses;

        } catch (Exception e) {
            log.error("Ошибка при получении заказов по статусу {}: {}", status, e.getMessage(), e);
            orderErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении заказов по статусу");
        }
    }

    @Transactional(readOnly = true)
    public long getOrderCountByStatus(OrderStatus status) {
        try {
            long count = orderRepository.countByStatus(status);
            log.debug("Количество заказов со статусом {}: {}", status, count);
            return count;
        } catch (Exception e) {
            log.error("Ошибка при подсчете заказов по статусу {}: {}", status, e.getMessage(), e);
            orderErrorCounter.increment();
            return 0;
        }
    }
}
