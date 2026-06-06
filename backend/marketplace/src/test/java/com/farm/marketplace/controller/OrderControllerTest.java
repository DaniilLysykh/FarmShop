package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.config.MethodSecurityTestConfig;
import com.farm.marketplace.exception.GlobalExceptionHandler;
import com.farm.marketplace.model.OrderStatus;
import com.farm.marketplace.payload.request.OrderRequest;
import com.farm.marketplace.payload.response.OrderResponse;
import com.farm.marketplace.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, MethodSecurityTestConfig.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private final OrderResponse sampleOrder = OrderResponse.builder()
            .id(1L)
            .status(OrderStatus.PENDING)
            .address("Street 1")
            .totalPrice(new BigDecimal("500.00"))
            .createdAt(LocalDateTime.now())
            .items(List.of())
            .canFarmerUpdateStatus(true)
            .canCustomerConfirmReceipt(false)
            .build();

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void createOrder_success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAddress("Street 1");
        request.setPhone("+79990001122");

        when(orderService.createOrder(any(OrderRequest.class), eq("buyer@test.com"))).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void getMyOrders_success() throws Exception {
        when(orderService.getMyOrders("buyer@test.com")).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void getFarmerOrders_success() throws Exception {
        when(orderService.getFarmerIncomingOrders("farmer@test.com")).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/orders/farmer/incoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void updateOrderStatus_success() throws Exception {
        OrderResponse updated = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.ACCEPTED)
                .address("Street 1")
                .totalPrice(new BigDecimal("500.00"))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .canFarmerUpdateStatus(true)
                .canCustomerConfirmReceipt(false)
                .build();

        when(orderService.updateOrderStatus(1L, "ACCEPTED", "farmer@test.com")).thenReturn(updated);

        mockMvc.perform(put("/api/orders/1/status").param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void confirmReceipt_success() throws Exception {
        OrderResponse completed = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.COMPLETED)
                .address("Street 1")
                .totalPrice(new BigDecimal("500.00"))
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .canFarmerUpdateStatus(false)
                .canCustomerConfirmReceipt(false)
                .build();

        when(orderService.confirmReceipt(1L, "buyer@test.com")).thenReturn(completed);

        mockMvc.perform(put("/api/orders/1/confirm-receipt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void createOrder_asFarmer_returnsForbidden() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAddress("Street 1");
        request.setPhone("+79990001122");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
