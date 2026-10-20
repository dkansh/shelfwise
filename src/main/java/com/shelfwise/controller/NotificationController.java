package com.shelfwise.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.model.Notification;
import com.shelfwise.service.AuthService;
import com.shelfwise.service.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public List<Notification> myNotifications() {
        return notificationService.getForMember(authService.getCurrentMemberId());
    }

    @GetMapping("/me/unread-count")
    public Map<String, Long> unreadCount() {
        return Map.of("unread", notificationService.countUnread(authService.getCurrentMemberId()));
    }

    @PostMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @PostMapping("/me/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllAsRead(authService.getCurrentMemberId());
        return ResponseEntity.ok().build();
    }
}
