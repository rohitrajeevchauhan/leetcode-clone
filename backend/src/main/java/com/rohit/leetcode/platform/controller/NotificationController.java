package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.NotificationResponse;
import com.rohit.leetcode.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getMyNotifications() {
        return notificationService.getMyNotifications();
    }

    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/unread/count")
    public long getUnreadCount() {
        return notificationService.getUnreadCount();
    }

    @PutMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable Long notificationId) {

        return notificationService.markAsRead(notificationId);
    }

    @PutMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead();
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(
            @PathVariable Long notificationId) {

        notificationService.deleteNotification(notificationId);
    }
}