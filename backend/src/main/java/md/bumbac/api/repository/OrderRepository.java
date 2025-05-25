package md.bumbac.api.repository;

import md.bumbac.api.model.Order;
import md.bumbac.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Хранение заказов.
 * Все базовые CRUD-методы (findById, save, delete…) унаследованы от JpaRepository.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Получить все заказы конкретного пользователя – для личного кабинета */
    List<Order> findByUser(User user);

    /** Получить заказы по статусу – удобно складскому сотруднику */
    List<Order> findByStatus(String status);
}
