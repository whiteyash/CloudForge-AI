package ai.cloudforge.api.ai.operations;

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
@Table(name = "remediation_plans")
public class RemediationPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private int confidence = 95;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String evidence;

    @Column(name = "risk_assessment", nullable = false, columnDefinition = "TEXT")
    private String riskAssessment;

    @Column(name = "rollback_plan", nullable = false, columnDefinition = "TEXT")
    private String rollbackPlan;

    @Column(name = "estimated_impact", nullable = false, length = 255)
    private String estimatedImpact;

    @Column(name = "required_permissions", nullable = false, length = 255)
    private String requiredPermissions;

    @Column(length = 30)
    private String status = "PENDING_APPROVAL";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RemediationPlan() {
    }

    public RemediationPlan(
            UUID projectId, UUID userId, String title, String targetType,
            String summary, int confidence, String evidence, String riskAssessment,
            String rollbackPlan, String estimatedImpact, String requiredPermissions, String status) {
        this.projectId = projectId;
        this.userId = userId;
        this.title = title;
        this.targetType = targetType;
        this.summary = summary;
        this.confidence = confidence;
        this.evidence = evidence;
        this.riskAssessment = riskAssessment;
        this.rollbackPlan = rollbackPlan;
        this.estimatedImpact = estimatedImpact;
        this.requiredPermissions = requiredPermissions;
        this.status = status != null ? status : "PENDING_APPROVAL";
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getSummary() {
        return summary;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public String getRollbackPlan() {
        return rollbackPlan;
    }

    public String getEstimatedImpact() {
        return estimatedImpact;
    }

    public String getRequiredPermissions() {
        return requiredPermissions;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
