package ai.cloudforge.api.pipeline;

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
@Table(name = "pipeline_runs")
public class PipelineRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pipeline_id", nullable = false)
    private UUID pipelineId;

    @Column(name = "run_number", nullable = false)
    private Integer runNumber;

    @Column(length = 30)
    private String status = "QUEUED";

    @Column(name = "triggered_by", nullable = false, length = 100)
    private String triggeredBy;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PipelineRun() {
    }

    public PipelineRun(UUID pipelineId, Integer runNumber, String triggeredBy) {
        this.pipelineId = pipelineId;
        this.runNumber = runNumber;
        this.triggeredBy = triggeredBy;
        this.correlationId = UUID.randomUUID();
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

    public UUID getPipelineId() {
        return pipelineId;
    }

    public Integer getRunNumber() {
        return runNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public UUID getCorrelationId() {
        return correlationId;
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

    public void setStatus(String status) {
        this.status = status;
        if ("RUNNING".equals(status) && startedAt == null) {
            this.startedAt = Instant.now();
        }
        if (("SUCCEEDED".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status)) && completedAt == null) {
            this.completedAt = Instant.now();
        }
    }
}
