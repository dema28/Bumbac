package com.bumbac.order.repository;

import com.bumbac.order.entity.Order;
import com.bumbac.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для доступа к заказам пользователей.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Получает список заказов, созданных указанным пользователем.
     *
     * @param user пользователь
     * @return список заказов
     */
    List<Order> findByUser(User user);

    /**
     * Получает заказ по ID, если он принадлежит пользователю.
     *
     * @param orderId ID заказа
     * @param userId  ID пользователя
     * @return заказ, если найден и принадлежит пользователю
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findWithUserById(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
     /**
     * Получает список заказов, созданных пользователем с указанным email.
     *
     * @param email email пользователя
     * @return список заказов
     */

