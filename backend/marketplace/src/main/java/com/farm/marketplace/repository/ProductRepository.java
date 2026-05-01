package com.farm.marketplace.repository;

import com.farm.marketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.jpa.repository.Query;
 import org.springframework.data.repository.query.Param;
 import com.farm.marketplace.model.Category;
 import java.math.BigDecimal;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Этот метод нам понадобится для эндпоинта "Мои товары" фермера
    List<Product> findAllByFarmerId(Long farmerId);

    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:farmerId IS NULL OR p.farmer.id = :farmerId) AND " +
            "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findProductsPublic(@Param("category") Category category,
                                     @Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     @Param("farmerId") Long farmerId,
                                     @Param("search") String search,
                                     Pageable pageable);
}



