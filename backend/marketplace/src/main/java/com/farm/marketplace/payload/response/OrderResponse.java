package com.farm.marketplace.payload.response;

import com.farm.marketplace.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private String address;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private boolean canFarmerUpdateStatus;
    private boolean canCustomerConfirmReceipt;
}