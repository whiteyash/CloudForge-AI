package ai.cloudforge.api.notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndStatus(userId, "UNREAD");
    }

    @Transactional
    public NotificationResponse markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.markAsRead();
        return NotificationResponse.fromEntity(notification);
    }

    @Transactional
    public NotificationResponse archiveNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.archive();
        return NotificationResponse.fromEntity(notification);
    }

    @Transactional
    public NotificationResponse restoreNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.markAsRead();
        return NotificationResponse.fromEntity(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Transactional
    public void deleteNotification(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    @Transactional
    public NotificationResponse createNotification(UUID userId, String title, String message, String type, String linkUrl) {
        Notification notification = notificationRepository.save(new Notification(userId, title, message, type, linkUrl));
        return NotificationResponse.fromEntity(notification);
    }

    public record NotificationResponse(
            UUID id,
            UUID userId,
            String title,
            String message,
            String type,
            String status,
            String linkUrl,
            Instant createdAt
    ) {
        public static NotificationResponse fromEntity(Notification n) {
            return new NotificationResponse(
                    n.getId(),
                    n.getUserId(),
                    n.getTitle(),
                    n.getMessage(),
                    n.getType(),
                    n.getStatus(),
                    n.getLinkUrl(),
                    n.getCreatedAt()
            );
        }
    }
}
