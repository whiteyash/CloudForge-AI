package ai.cloudforge.api.aiops;

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
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 30)
    private String severity = "HIGH";

    @Column(length = 30)
    private String status = "OPEN";

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "confidence_score")
    private Double confidenceScore = 0.85;

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected Incident() {
    }

    public Incident(UUID projectId, String title, String severity, String rootCause, Double confidenceScore) {
        this.projectId = projectId;
        this.title = title;
        this.severity = severity != null ? severity : "HIGH";
        this.rootCause = rootCause;
        this.confidenceScore = confidenceScore != null ? confidenceScore : 0.85;
    }

    @PrePersist
    void onCreate() {
        if (detectedAt == null) {
            detectedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public String getRootCause() {
        return rootCause;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void resolve() {
        this.status = "RESOLVED";
        this.resolvedAt = Instant.now();
    }
}
