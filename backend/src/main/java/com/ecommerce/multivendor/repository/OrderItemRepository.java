package com.ecommerce.multivendor.repository;

import com.ecommerce.multivendor.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByProductSellerId(Long sellerId);

    @Query("SELECT cast(oi.order.createdAt as date) as date, SUM(oi.quantity) as count FROM OrderItem oi WHERE oi.product.seller.id = :sellerId AND oi.order.createdAt >= :startDate GROUP BY cast(oi.order.createdAt as date) ORDER BY cast(oi.order.createdAt as date) ASC")
    List<Object[]> countOrderItemsByDateForSellerAfter(@Param("sellerId") Long sellerId, @Param("startDate") LocalDateTime startDate);
}
