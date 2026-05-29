package com.farm.marketplace.model;

public enum OrderStatus {
    PENDING,          // Ожидает подтверждения
    ACCEPTED,         // Принят фермером
    READY_FOR_PICKUP, // Готов к выдаче (или доставке)
    DELIVERED,        // Передан покупателю — ждёт подтверждения получения
    COMPLETED,        // Покупатель подтвердил получение
    CANCELLED         // Отменен
}