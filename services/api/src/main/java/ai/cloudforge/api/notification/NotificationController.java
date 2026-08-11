package ai.cloudforge.api.notification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.notification.NotificationService.NotificationResponse;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> listNotifications(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(principal.userId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(principal.userId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(principal.userId(), id));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<NotificationResponse> archiveNotification(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.archiveNotification(principal.userId(), id));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<NotificationResponse> restoreNotification(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.restoreNotification(principal.userId(), id));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AuthPrincipal principal) {
        notificationService.markAllAsRead(principal.userId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID id) {
        notificationService.deleteNotification(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }
}
