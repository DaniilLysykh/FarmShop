package com.farm.marketplace.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private Long id; // ID самой записи в корзине
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
}