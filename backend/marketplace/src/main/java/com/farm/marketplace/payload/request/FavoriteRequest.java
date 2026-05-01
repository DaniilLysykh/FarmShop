package com.farm.marketplace.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {
    @NotNull(message = "ID товара обязателен")
    private Long productId;
}