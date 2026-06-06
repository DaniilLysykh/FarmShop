package com.farm.marketplace.controller;

import com.farm.marketplace.controller.support.ControllerTestSetup;
import com.farm.marketplace.model.NotificationType;
import com.farm.marketplace.payload.response.NotificationResponse;
import com.farm.marketplace.payload.response.UnreadCountResponse;
import com.farm.marketplace.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@ControllerTestSetup
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private final NotificationResponse sampleNotification = NotificationResponse.builder()
            .id(1L)
            .type(NotificationType.ORDER_STATUS)
            .title("Статус заказа")
            .message("Заказ #1 принят")
            .referenceId(1L)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    @WithMockUser(username = "user@test.com")
    void getNotifications_returnsList() throws Exception {
        when(notificationService.getNotifications("user@test.com")).thenReturn(List.of(sampleNotification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Статус заказа"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void getUnreadCount_returnsCount() throws Exception {
        when(notificationService.getUnreadCount("user@test.com")).thenReturn(new UnreadCountResponse(3));

        mockMvc.perform(get("/api/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void markAsRead_success() throws Exception {
        when(notificationService.markAsRead(1L, "user@test.com")).thenReturn(sampleNotification);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void markAllAsRead_success() throws Exception {
        mockMvc.perform(put("/api/notifications/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Все уведомления прочитаны"));

        verify(notificationService).markAllAsRead("user@test.com");
    }
}
