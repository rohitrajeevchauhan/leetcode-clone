package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.dto.NotificationResponse;
import com.rohit.leetcode.platform.entity.Notification;
import com.rohit.leetcode.platform.repository.NotificationRepository;
import com.rohit.leetcode.platform.service.NotificationService;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {

        User user = getCurrentUser();

        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        return notificationRepository
                .countByUserIdAndReadFalse(
                        getCurrentUser().getId()
                );
    }

    @Override
    public NotificationResponse markAsRead(
            Long notificationId) {

        User user = getCurrentUser();

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        validateOwner(notification.getUser(), user);

        notification.setRead(true);

        return mapToResponse(
                notificationRepository.save(notification)
        );
    }

    @Override
    public void markAllAsRead() {

        User user = getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                                user.getId()
                        );

        notifications.forEach(
                notification -> notification.setRead(true)
        );

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(
            Long notificationId) {

        User user = getCurrentUser();

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        validateOwner(notification.getUser(), user);

        notificationRepository.delete(notification);
    }

    @Override
    public void createNotification(
            Long userId,
            String type,
            String title,
            String message,
            String referenceType,
            Long referenceId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        Notification notification =
                Notification.builder()
                        .user(user)
                        .type(type)
                        .title(title)
                        .message(message)
                        .referenceType(referenceType)
                        .referenceId(referenceId)
                        .read(false)
                        .build();

        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .read(Boolean.TRUE.equals(notification.getRead()))
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private void validateOwner(
            User owner,
            User currentUser) {

        if (!owner.getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You cannot access this notification"
            );
        }
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        ));
    }
}