package com.bumbac.modules.order.repository;

import com.bumbac.modules.order.entity.Order;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.shared.enums.OrderStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для доступа к заказам пользователей.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * Получает список заказов, созданных указанным пользователем.
   *
   * @param user пользователь
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'user_' + #user.id")
  List<Order> findByUser(User user);

  /**
   * Получает заказ по ID, если он принадлежит пользователю.
   *
   * @param orderId ID заказа
   * @param userId  ID пользователя
   * @return заказ, если найден и принадлежит пользователю
   */
  @Cacheable(value = "orders", key = "'order_' + #orderId + '_user_' + #userId")
  @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :orderId AND o.user.id = :userId")
  Optional<Order> findWithUserById(@Param("orderId") Long orderId, @Param("userId") Long userId);

  /**
   * Получает список заказов по статусу.
   *
   * @param status статус заказа
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'status_' + #status")
  List<Order> findByStatus(OrderStatus status);

  /**
   * Получает список заказов по статусу и пользователю.
   *
   * @param status статус заказа
   * @param user   пользователь
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'status_' + #status + '_user_' + #user.id")
  List<Order> findByStatusAndUser(OrderStatus status, User user);

  /**
   * Получает список заказов, созданных в указанном периоде.
   *
   * @param from дата начала периода
   * @param to   дата окончания периода
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'period_' + #from + '_' + #to")
  @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :from AND :to ORDER BY o.createdAt DESC")
  List<Order> findByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  /**
   * Получает список заказов пользователя, созданных в указанном периоде.
   *
   * @param user пользователь
   * @param from дата начала периода
   * @param to   дата окончания периода
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'user_' + #user.id + '_period_' + #from + '_' + #to")
  @Query("SELECT o FROM Order o WHERE o.user = :user AND o.createdAt BETWEEN :from AND :to ORDER BY o.createdAt DESC")
  List<Order> findByUserAndCreatedAtBetween(@Param("user") User user, @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  /**
   * Получает список заказов с общей суммой выше указанной.
   *
   * @param minAmount минимальная сумма
   * @return список заказов
   */
  @Cacheable(value = "orders", key = "'min_amount_' + #minAmount")
  @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minAmount ORDER BY o.totalAmount DESC")
  List<Order> findByTotalAmountGreaterThanEqual(@Param("minAmount") BigDecimal minAmount);

  /**
   * Подсчитывает количество заказов по статусу.
   *
   * @param status статус заказа
   * @return количество заказов
   */
  @Cacheable(value = "orders", key = "'count_status_' + #status")
  long countByStatus(OrderStatus status);

  /**
   * Подсчитывает количество заказов пользователя.
   *
   * @param user пользователь
   * @return количество заказов
   */
  @Cacheable(value = "orders", key = "'count_user_' + #user.id")
  long countByUser(User user);

  /**
   * Получает все заказы с оптимизированными запросами.
   *
   * @return список всех заказов
   */
  @Cacheable(value = "orders", key = "#root.methodName")
  @Query("SELECT o FROM Order o JOIN FETCH o.user ORDER BY o.createdAt DESC")
  List<Order> findAllWithUser();

  /**
   * Получает заказ по ID с оптимизированным запросом.
   *
   * @param id ID заказа
   * @return заказ
   */
  @Override
  @Cacheable(value = "orders", key = "#id")
  @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items WHERE o.id = :id")
  Optional<Order> findById(@Param("id") Long id);

  // Инвалидация кэша при изменениях
  @Override
  @CacheEvict(value = { "orders" }, allEntries = true)
  <S extends Order> S save(S entity);

  @Override
  @CacheEvict(value = { "orders" }, allEntries = true)
  <S extends Order> List<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = { "orders" }, allEntries = true)
  void deleteById(Long id);

  @Override
  @CacheEvict(value = { "orders" }, allEntries = true)
  void delete(Order entity);

  @Override
  @CacheEvict(value = { "orders" }, allEntries = true)
  void deleteAllById(Iterable<? extends Long> ids);
}
