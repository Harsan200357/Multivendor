package com.ecommerce.multivendor.repository;

import com.ecommerce.multivendor.entity.Order;
import com.ecommerce.multivendor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT cast(o.createdAt as date) as date, COUNT(o) as count FROM Order o WHERE o.createdAt >= :startDate GROUP BY cast(o.createdAt as date) ORDER BY cast(o.createdAt as date) ASC")
    List<Object[]> countOrdersByDateAfter(@Param("startDate") LocalDateTime startDate);
}
