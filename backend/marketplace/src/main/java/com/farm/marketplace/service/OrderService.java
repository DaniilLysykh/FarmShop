package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.NotificationType;
import com.farm.marketplace.model.*;
import com.farm.marketplace.payload.request.OrderRequest;
import com.farm.marketplace.payload.response.OrderItemResponse;
import com.farm.marketplace.payload.response.OrderResponse;
import com.farm.marketplace.repository.CartItemRepository;
import com.farm.marketplace.repository.OrderRepository;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private OrderResponse mapToResponse(Order order, Long farmerId) {
        List<OrderItemResponse> items = order.getItems().stream()
                // Фильтруем: если farmerId задан, оставляем только его товары
                .filter(item -> farmerId == null || item.getProduct().getFarmer().getId().equals(farmerId))
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .address(order.getAddress())
                // ВАЖНО: Сумма заказа для фермера тоже должна быть только за его товары
                .totalPrice(farmerId == null ? order.getTotalPrice() : calculateSubTotal(items))
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private BigDecimal calculateSubTotal(List<OrderItemResponse> items) {
        return items.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional // Очень важно! Если что-то пойдет не так, все изменения откатятся
    public OrderResponse createOrder(OrderRequest request, String email) {
        User buyer = getUserByEmail(email);
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(buyer.getId());

        if (cartItems.isEmpty()) {
            throw new AppException("Корзина пуста", HttpStatus.BAD_REQUEST);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(buyer)
                .status(OrderStatus.PENDING)
                .address(request.getAddress())
                // В реальном проекте тут бы еще сохранить телефон, если мы добавим это поле в модель Order
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Проверяем, хватает ли товара
            if (product.getStock() < cartItem.getQuantity()) {
                throw new AppException("Недостаточно товара: " + product.getName(), HttpStatus.BAD_REQUEST);
            }

            // Списываем остаток товара
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Создаем элемент заказа со снапшотом цены
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getItems().add(orderItem);

            // Считаем общую сумму: цена * количество
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllByUserId(buyer.getId());

        Set<User> farmersToNotify = new HashSet<>();
        for (OrderItem item : savedOrder.getItems()) {
            farmersToNotify.add(item.getProduct().getFarmer());
        }
        for (User farmer : farmersToNotify) {
            notificationService.createNotification(
                    farmer,
                    NotificationType.ORDER_NEW,
                    "Новый заказ",
                    "Поступил заказ #" + savedOrder.getId() + " на сумму " + savedOrder.getTotalPrice() + " руб.",
                    savedOrder.getId()
            );
        }

        notificationService.createNotification(
                buyer,
                NotificationType.ORDER_STATUS,
                "Заказ оформлен",
                "Ваш заказ #" + savedOrder.getId() + " принят и ожидает подтверждения фермера.",
                savedOrder.getId()
        );

        // Покупатель видит весь заказ
        return mapToResponse(savedOrder, null);
    }

    public List<OrderResponse> getMyOrders(String email) {
        User buyer = getUserByEmail(email);
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(buyer.getId()).stream()
                .map(order -> mapToResponse(order, null)) // Покупатель видит всё
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getFarmerIncomingOrders(String email) {
        User farmer = getUserByEmail(email);
        return orderRepository.findOrdersByFarmerId(farmer.getId()).stream()
                .map(order -> mapToResponse(order, farmer.getId())) // ФЕРМЕР ВИДИТ ТОЛЬКО СВОЁ
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus, String email) {
        User farmer = getUserByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Заказ не найден", HttpStatus.NOT_FOUND));

        // Проверяем, есть ли в этом заказе товары текущего фермера
        boolean hasFarmerItems = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getFarmer().getId().equals(farmer.getId()));

        if (!hasFarmerItems) {
            throw new AppException("У вас нет прав изменять статус этого заказа", HttpStatus.FORBIDDEN);
        }

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new AppException("Неверный статус заказа", HttpStatus.BAD_REQUEST);
        }

        Order saved = orderRepository.save(order);
        String statusLabel = getStatusLabel(status);
        notificationService.createNotification(
                order.getUser(),
                NotificationType.ORDER_STATUS,
                "Статус заказа изменён",
                "Заказ #" + saved.getId() + ": " + statusLabel,
                saved.getId()
        );

        return mapToResponse(saved, farmer.getId());
    }

    private String getStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "ожидает подтверждения";
            case ACCEPTED -> "принят в работу";
            case READY_FOR_PICKUP -> "готов к выдаче";
            case DELIVERED -> "выполнен";
            case CANCELLED -> "отменён";
        };
    }
}