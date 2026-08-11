package ai.cloudforge.api.observability;

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
@Table(name = "system_alerts")
public class SystemAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "alert_name", nullable = false, length = 150)
    private String alertName;

    @Column(length = 30)
    private String severity = "WARNING";

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SystemAlert() {
    }

    public SystemAlert(UUID projectId, String alertName, String severity, String message) {
        this.projectId = projectId;
        this.alertName = alertName;
        this.severity = severity != null ? severity : "WARNING";
        this.message = message;
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

    public UUID getProjectId() {
        return projectId;
    }

    public String getAlertName() {
        return alertName;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void acknowledge() {
        this.status = "ACKNOWLEDGED";
    }

    public void resolve() {
        this.status = "RESOLVED";
    }
}
