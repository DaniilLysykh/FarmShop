package com.farm.marketplace.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    @NotBlank(message = "Адрес доставки (или самовывоза) обязателен")
    private String address;

    @NotBlank(message = "Контактный телефон обязателен")
    private String phone;
}