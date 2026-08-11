package ai.cloudforge.api.ai.prediction;

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
@Table(name = "risk_forecasts")
public class RiskForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "risk_factor", nullable = false, length = 150)
    private String riskFactor;

    @Column(length = 30)
    private String severity = "MEDIUM";

    @Column(name = "mitigation_recommendation", nullable = false, columnDefinition = "TEXT")
    private String mitigationRecommendation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RiskForecast() {
    }

    public RiskForecast(UUID projectId, String riskFactor, String severity, String mitigationRecommendation) {
        this.projectId = projectId;
        this.riskFactor = riskFactor;
        this.severity = severity != null ? severity : "MEDIUM";
        this.mitigationRecommendation = mitigationRecommendation;
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

    public String getRiskFactor() {
        return riskFactor;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMitigationRecommendation() {
        return mitigationRecommendation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
