package ai.cloudforge.api.deployment;

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
@Table(name = "deployment_rollbacks")
public class DeploymentRollback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "target_deployment_id", nullable = false)
    private UUID targetDeploymentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "initiated_by", nullable = false, length = 100)
    private String initiatedBy;

    @Column(length = 30)
    private String status = "COMPLETED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DeploymentRollback() {
    }

    public DeploymentRollback(UUID deploymentId, UUID targetDeploymentId, String reason, String initiatedBy) {
        this.deploymentId = deploymentId;
        this.targetDeploymentId = targetDeploymentId;
        this.reason = reason;
        this.initiatedBy = initiatedBy;
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

    public UUID getDeploymentId() {
        return deploymentId;
    }

    public UUID getTargetDeploymentId() {
        return targetDeploymentId;
    }

    public String getReason() {
        return reason;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
