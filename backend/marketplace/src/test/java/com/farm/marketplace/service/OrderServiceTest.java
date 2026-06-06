package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.*;
import com.farm.marketplace.repository.CartItemRepository;
import com.farm.marketplace.repository.OrderRepository;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.UserRepository;
import com.farm.marketplace.payload.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void confirmReceipt_success_setsCompleted_and_notifiesFarmerAndBuyer() {
        // Arrange
        String email = "buyer@test.com";
        long buyerId = 1L;
        long farmerId = 2L;
        long orderId = 100L;

        User buyer = User.builder()
                .id(buyerId)
                .email(email)
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        User farmer = User.builder()
                .id(farmerId)
                .email("farmer@test.com")
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Milk")
                .description(null)
                .price(new BigDecimal("10.50"))
                .unit("l")
                .stock(10)
                .category(Category.MILK)
                .farmer(farmer)
                .imageUrl(null)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("10.50"))
                .order(null) // не нужно для логики теста
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(buyer)
                .status(OrderStatus.DELIVERED)
                .address("Address")
                .totalPrice(new BigDecimal("21.00"))
                .createdAt(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(buyer));
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.confirmReceipt(orderId, email);

        // Assert
        assertEquals(OrderStatus.COMPLETED, response.getStatus());

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(2)).createNotification(
                any(User.class),
                eq(NotificationType.ORDER_STATUS),
                titleCaptor.capture(),
                anyString(),
                eq(orderId)
        );

        assertTrue(titleCaptor.getAllValues().contains("Заказ завершён"));
        assertTrue(titleCaptor.getAllValues().contains("Получение подтверждено"));
    }

    @Test
    void confirmReceipt_wrongStatus_throwsBadRequest() {
        // Arrange
        String email = "buyer@test.com";
        long buyerId = 1L;
        long orderId = 101L;

        User buyer = User.builder()
                .id(buyerId)
                .email(email)
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(buyer)
                .status(OrderStatus.ACCEPTED)
                .address("Address")
                .totalPrice(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(buyer));
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));

        // Act + Assert
        AppException ex = assertThrows(AppException.class, () -> orderService.confirmReceipt(orderId, email));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void confirmReceipt_wrongUser_throwsForbidden() {
        // Arrange
        String email = "buyer@test.com";
        long orderId = 102L;

        User buyer = User.builder()
                .id(1L)
                .email(email)
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        User otherUser = User.builder()
                .id(999L)
                .email("other@test.com")
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(otherUser)
                .status(OrderStatus.DELIVERED)
                .address("Address")
                .totalPrice(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(buyer));
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));

        // Act + Assert
        AppException ex = assertThrows(AppException.class, () -> orderService.confirmReceipt(orderId, email));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void updateOrderStatus_farmerCannotUpdate_whenCurrentStatusDelivered() {
        // Arrange
        String email = "farmer@test.com";
        long farmerId = 2L;
        long orderId = 200L;

        User farmer = User.builder()
                .id(farmerId)
                .email(email)
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Milk")
                .description(null)
                .price(new BigDecimal("10.50"))
                .unit("l")
                .stock(10)
                .category(Category.MILK)
                .farmer(farmer)
                .imageUrl(null)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("10.50"))
                .order(null)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(User.builder().id(1L).email("buyer@test.com").passwordHash("hash").roles(null).createdAt(LocalDateTime.now()).build())
                .status(OrderStatus.DELIVERED)
                .address("Address")
                .totalPrice(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(farmer));
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));

        // Act + Assert
        AppException ex = assertThrows(
                AppException.class,
                () -> orderService.updateOrderStatus(orderId, "ACCEPTED", email)
        );
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void updateOrderStatus_farmerWithoutItsItems_throwsForbidden() {
        // Arrange
        String email = "farmer@test.com";
        long farmerId = 2L;
        long orderId = 201L;

        User farmer = User.builder()
                .id(farmerId)
                .email(email)
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        User otherFarmer = User.builder()
                .id(3L)
                .email("other_farmer@test.com")
                .passwordHash("hash")
                .roles(null)
                .createdAt(LocalDateTime.now())
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("Milk")
                .description(null)
                .price(new BigDecimal("10.50"))
                .unit("l")
                .stock(10)
                .category(Category.MILK)
                .farmer(otherFarmer)
                .imageUrl(null)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("10.50"))
                .order(null)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(User.builder().id(1L).email("buyer@test.com").passwordHash("hash").roles(null).createdAt(LocalDateTime.now()).build())
                .status(OrderStatus.PENDING)
                .address("Address")
                .totalPrice(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .items(List.of(item))
                .build();

        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(farmer));
        when(orderRepository.findById(eq(orderId))).thenReturn(Optional.of(order));

        // Act + Assert
        AppException ex = assertThrows(
                AppException.class,
                () -> orderService.updateOrderStatus(orderId, "ACCEPTED", email)
        );
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }
}

