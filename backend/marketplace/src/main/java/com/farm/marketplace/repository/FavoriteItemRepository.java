package com.farm.marketplace.repository;

import com.farm.marketplace.model.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    List<FavoriteItem> findAllByUserId(Long userId);
    Optional<FavoriteItem> findByUserIdAndProductId(Long userId, Long productId);
}