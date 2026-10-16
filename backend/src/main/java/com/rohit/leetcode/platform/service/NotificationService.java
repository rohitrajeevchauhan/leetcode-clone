package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications();

    List<NotificationResponse> getUnreadNotifications();

    long getUnreadCount();

    NotificationResponse markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);

    void createNotification(
            Long userId,
            String type,
            String title,
            String message,
            String referenceType,
            Long referenceId
    );
}