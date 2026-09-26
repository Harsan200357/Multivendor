package com.ecommerce.multivendor.repository;

import com.ecommerce.multivendor.entity.Cart;
import com.ecommerce.multivendor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
