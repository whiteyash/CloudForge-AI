package ai.cloudforge.api.ai.rca;

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
@Table(name = "root_cause_reports")
public class RootCauseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "incident_id")
    private UUID incidentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "root_cause", nullable = false, columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "confidence_score", nullable = false)
    private int confidenceScore;

    @Column(name = "risk_rating", length = 30)
    private String riskRating = "HIGH";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RootCauseReport() {
    }

    public RootCauseReport(UUID projectId, UUID incidentId, String summary, String rootCause, int confidenceScore, String riskRating) {
        this.projectId = projectId;
        this.incidentId = incidentId;
        this.summary = summary;
        this.rootCause = rootCause;
        this.confidenceScore = confidenceScore;
        this.riskRating = riskRating != null ? riskRating : "HIGH";
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

    public UUID getIncidentId() {
        return incidentId;
    }

    public String getSummary() {
        return summary;
    }

    public String getRootCause() {
        return rootCause;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public String getRiskRating() {
        return riskRating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
