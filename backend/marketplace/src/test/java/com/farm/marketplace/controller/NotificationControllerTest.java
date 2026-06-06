package com.farm.marketplace.controller;

import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.payload.response.UnreadCountResponse;
import com.farm.marketplace.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock private NotificationService notificationService;
    @InjectMocks private NotificationController notificationController;

    @Test
    void getUnreadCount_returnsCount() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@test.com");
        when(notificationService.getUnreadCount("user@test.com")).thenReturn(new UnreadCountResponse(4));

        var response = notificationController.getUnreadCount(auth);

        assertEquals(4L, response.getBody().getCount());
    }

    @Test
    void markAllAsRead_returnsMessage() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@test.com");

        var response = notificationController.markAllAsRead(auth);

        verify(notificationService).markAllAsRead("user@test.com");
        assertEquals("Все уведомления прочитаны", ((MessageResponse) response.getBody()).getMessage());
    }
}
