package ai.cloudforge.api.deployment;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "deployments")
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "pipeline_run_id")
    private UUID pipelineRunId;

    @Column(name = "artifact_id")
    private UUID artifactId;

    @Column(name = "target_name", nullable = false, length = 100)
    private String targetName;

    @Column(length = 50)
    private String strategy = "ROLLING";

    @Column(length = 30)
    private String status = "QUEUED";

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Deployment() {
    }

    public Deployment(UUID projectId, UUID pipelineRunId, UUID artifactId, String targetName, String strategy, String idempotencyKey, String requestedBy) {
        this.projectId = projectId;
        this.pipelineRunId = pipelineRunId;
        this.artifactId = artifactId;
        this.targetName = targetName;
        this.strategy = strategy != null ? strategy : "ROLLING";
        this.idempotencyKey = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        this.requestedBy = requestedBy;
        this.status = "PRODUCTION".equalsIgnoreCase(targetName) ? "PENDING_APPROVAL" : "DEPLOYING";
        if ("DEPLOYING".equals(this.status)) {
            this.startedAt = Instant.now();
        }
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getPipelineRunId() {
        return pipelineRunId;
    }

    public UUID getArtifactId() {
        return artifactId;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void approve(String approverEmail) {
        this.approvedBy = approverEmail;
        this.status = "DEPLOYING";
        this.startedAt = Instant.now();
    }

    public void setStatus(String status) {
        this.status = status;
        if (("SUCCEEDED".equals(status) || "FAILED".equals(status) || "ROLLED_BACK".equals(status) || "CANCELLED".equals(status)) && completedAt == null) {
            this.completedAt = Instant.now();
        }
    }
}
