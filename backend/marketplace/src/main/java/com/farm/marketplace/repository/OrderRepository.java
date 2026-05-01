package com.farm.marketplace.repository;

import com.farm.marketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Получить все заказы покупателя (с сортировкой от новых к старым)
    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // Хитрый запрос: ищем все уникальные заказы, в которых есть товары определенного фермера
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.product.farmer.id = :farmerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersByFarmerId(@Param("farmerId") Long farmerId);
}