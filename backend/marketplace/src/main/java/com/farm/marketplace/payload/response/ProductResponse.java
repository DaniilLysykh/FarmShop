package com.farm.marketplace.payload.response;

import com.farm.marketplace.model.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String unit;
    private Integer stock;
    private Category category;
    private Long farmerId;
    private String imageUrl;
    private Double averageRating;
    private Long reviewCount;
}