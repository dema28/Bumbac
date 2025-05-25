package md.bumbac.api.repository;

import md.bumbac.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /* ---------- обычные выборки ---------- */

    List<Product> findByCategory_Slug(String slug);

    List<Product> findByColorNameContainingIgnoreCase(String color);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    /* ---------- мультиязычие ---------- */

    List<Product> findByLang(String lang);                 // 'ru', 'ro', 'en'

    /* ---------- блокировка при оформлении заказа ---------- */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findProductForUpdate(@Param("id") Long id);
}
