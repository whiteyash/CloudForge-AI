package ai.cloudforge.api.notification;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 40)
    private String type; // INFO | WARNING | CRITICAL | INVITATION

    @Column(nullable = false, length = 20)
    private String status = "UNREAD"; // UNREAD | READ | ARCHIVED

    @Column(name = "link_url", length = 255)
    private String linkUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(UUID userId, String title, String message, String type, String linkUrl) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.linkUrl = linkUrl;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markAsRead() {
        this.status = "READ";
    }

    public void archive() {
        this.status = "ARCHIVED";
    }
}
