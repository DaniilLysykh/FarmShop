package com.farm.marketplace.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "ID товара обязателен")
    private Long productId;

    @NotNull(message = "Оценка обязательна")
    @Min(value = 1, message = "Оценка от 1 до 5")
    @Max(value = 5, message = "Оценка от 1 до 5")
    private Integer rating;

    @Size(max = 2000, message = "Комментарий не более 2000 символов")
    private String comment;
}
