package com.ecommerce.multivendor.repository;

import com.ecommerce.multivendor.entity.Product;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeller(User seller);
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p WHERE " +
            "(:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchAndFilterProducts(@org.springframework.data.repository.query.Param("query") String query,
                                          @org.springframework.data.repository.query.Param("minPrice") java.math.BigDecimal minPrice,
                                          @org.springframework.data.repository.query.Param("maxPrice") java.math.BigDecimal maxPrice,
                                          @org.springframework.data.repository.query.Param("categoryId") Long categoryId);
}
