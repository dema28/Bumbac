package md.bumbac.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import md.bumbac.api.model.*;
import md.bumbac.api.repository.OrderRepository;
import md.bumbac.api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEmailService orderEmailService;

    /** заказы текущего пользователя */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /** все заказы (для ADMIN/CONTENT_MANAGER) */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /** создать заказ, уменьшив остаток и отправив письмо на склад */
    @Transactional
    public Order createOrder(Order order, User user) {

        order.setUser(user);
        order.setStatus(OrderStatus.CREATED.name());   // или храните Enum напрямую
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : order.getItems()) {

            Product product = productRepository.findProductForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found: " + item.getProduct().getId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            /* уменьшаем остаток */
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            /* фиксируем цену в момент покупки */
            item.setPrice(product.getPrice());

            total = total.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotal(total);

        /* сохраняем заказ и шлём письмо */
        Order saved = orderRepository.save(order);
        orderEmailService.sendOrderToWarehouse(saved);

        log.info("Заказ #{} успешно создан пользователем {}", saved.getId(), user.getEmail());
        return saved;
    }
}
