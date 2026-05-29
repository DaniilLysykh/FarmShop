package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.OrderRequest;
import com.farm.marketplace.payload.response.OrderResponse;
import com.farm.marketplace.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Оформить заказ (Только покупатель)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(request, authentication.getName()));
    }

    // История заказов покупателя
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName()));
    }

    // Входящие заказы для фермера
    @GetMapping("/farmer/incoming")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<OrderResponse>> getFarmerOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getFarmerIncomingOrders(authentication.getName()));
    }

    // Изменить статус заказа (Только фермер)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                           @RequestParam String status,
                                                           Authentication authentication) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, authentication.getName()));
    }

    // Подтвердить получение заказа (Только покупатель)
    @PutMapping("/{id}/confirm-receipt")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> confirmReceipt(@PathVariable Long id,
                                                        Authentication authentication) {
        return ResponseEntity.ok(orderService.confirmReceipt(id, authentication.getName()));
    }
}