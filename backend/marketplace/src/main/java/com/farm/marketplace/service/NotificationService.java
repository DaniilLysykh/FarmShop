package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.Notification;
import com.farm.marketplace.model.NotificationType;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.response.NotificationResponse;
import com.farm.marketplace.payload.response.UnreadCountResponse;
import com.farm.marketplace.repository.NotificationRepository;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Transactional
    public void createNotification(User user, NotificationType type, String title, String message, Long referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotifications(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UnreadCountResponse getUnreadCount(String email) {
        User user = getUserByEmail(email);
        long count = notificationRepository.countByUserIdAndReadFalse(user.getId());
        return new UnreadCountResponse(count);
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String email) {
        User user = getUserByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException("Уведомление не найдено", HttpStatus.NOT_FOUND));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AppException("Нет доступа", HttpStatus.FORBIDDEN);
        }

        notification.setRead(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = getUserByEmail(email);
        notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .filter(n -> !n.isRead())
                .forEach(n -> n.setRead(true));
    }
}
