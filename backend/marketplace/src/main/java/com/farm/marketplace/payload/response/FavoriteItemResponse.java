package com.farm.marketplace.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class FavoriteItemResponse {
    private Long id; // ID записи в таблице избранного
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
}