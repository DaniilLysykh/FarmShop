package com.farm.marketplace.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Название товара не может быть пустым")
    private String name;

    private String description;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть больше нуля")
    private BigDecimal price;

    @NotBlank(message = "Единица измерения обязательна (кг, л, шт)")
    private String unit;

    @NotNull(message = "Остаток обязателен")
    @Min(value = 0, message = "Остаток не может быть отрицательным")
    private Integer stock;

    @NotBlank(message = "Категория обязательна")
    private String category;
}