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
@Table(name = "execution_history")
public class ExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "executed_by", nullable = false)
    private UUID executedBy;

    @Column(name = "execution_service", nullable = false, length = 100)
    private String executionService;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String logs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ExecutionHistory() {
    }

    public ExecutionHistory(UUID planId, UUID executedBy, String executionService, String status, String logs) {
        this.planId = planId;
        this.executedBy = executedBy;
        this.executionService = executionService;
        this.status = status;
        this.logs = logs;
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

    public UUID getPlanId() {
        return planId;
    }

    public UUID getExecutedBy() {
        return executedBy;
    }

    public String getExecutionService() {
        return executionService;
    }

    public String getStatus() {
        return status;
    }

    public String getLogs() {
        return logs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
