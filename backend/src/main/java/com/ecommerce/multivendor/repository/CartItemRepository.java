package com.ecommerce.multivendor.repository;

import com.ecommerce.multivendor.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
