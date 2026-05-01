package com.farm.marketplace.model;

public enum OrderStatus {
    PENDING,          // Ожидает подтверждения
    ACCEPTED,         // Принят фермером
    READY_FOR_PICKUP, // Готов к выдаче (или доставке)
    DELIVERED,        // Доставлен / Выполнен
    CANCELLED         // Отменен
}