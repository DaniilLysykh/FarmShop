package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.Notification;
import com.farm.marketplace.model.NotificationType;
import com.farm.marketplace.model.User;
import com.farm.marketplace.repository.NotificationRepository;
import com.farm.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private NotificationService notificationService;

    @Test
    void getUnreadCount_returnsCount() {
        User user = user(1L, "user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(3L);

        var response = notificationService.getUnreadCount("user@test.com");

        assertEquals(3L, response.getCount());
    }

    @Test
    void markAsRead_otherUsersNotification_throwsForbidden() {
        User owner = user(1L, "owner@test.com");
        User other = user(2L, "other@test.com");
        Notification notification = Notification.builder()
                .id(10L).user(owner).type(NotificationType.ORDER_STATUS)
                .title("T").message("M").read(false).createdAt(LocalDateTime.now()).build();

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        AppException ex = assertThrows(AppException.class,
                () -> notificationService.markAsRead(10L, "other@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    private User user(Long id, String email) {
        return User.builder().id(id).email(email).passwordHash("hash")
                .createdAt(LocalDateTime.now()).build();
    }
}
