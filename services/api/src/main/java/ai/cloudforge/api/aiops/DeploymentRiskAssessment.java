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
@Table(name = "deployment_risk_assessments")
public class DeploymentRiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "risk_level", nullable = false, length = 30)
    private String riskLevel;

    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore;

    @Column(name = "risk_factors", nullable = false, columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DeploymentRiskAssessment() {
    }

    public DeploymentRiskAssessment(UUID projectId, UUID deploymentId, String riskLevel, Double confidenceScore, String riskFactors) {
        this.projectId = projectId;
        this.deploymentId = deploymentId;
        this.riskLevel = riskLevel;
        this.confidenceScore = confidenceScore;
        this.riskFactors = riskFactors;
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

    public UUID getDeploymentId() {
        return deploymentId;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public String getRiskFactors() {
        return riskFactors;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
