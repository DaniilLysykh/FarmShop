package com.farm.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String unit; // кг, л, шт

    @Column(nullable = false)
    private Integer stock; // остаток на складе

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // Связь: Много товаров могут принадлежать одному фермеру
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    // Поле для хранения ссылки на фотографию (добавим логику загрузки позже)
    @Column(name = "image_url")
    private String imageUrl;
}