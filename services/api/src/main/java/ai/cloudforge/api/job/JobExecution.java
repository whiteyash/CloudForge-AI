package ai.cloudforge.api.job;

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
@Table(name = "job_executions")
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pipeline_run_id", nullable = false)
    private UUID pipelineRunId;

    @Column(name = "job_name", nullable = false, length = 150)
    private String jobName;

    @Column(name = "runner_id")
    private UUID runnerId;

    @Column(length = 30)
    private String status = "QUEUED";

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "duration_ms")
    private Long durationMs = 0L;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected JobExecution() {
    }

    public JobExecution(UUID pipelineRunId, String jobName, UUID runnerId) {
        this.pipelineRunId = pipelineRunId;
        this.jobName = jobName;
        this.runnerId = runnerId;
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

    public UUID getPipelineRunId() {
        return pipelineRunId;
    }

    public String getJobName() {
        return jobName;
    }

    public UUID getRunnerId() {
        return runnerId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public Long getDurationMs() {
        return durationMs;
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

    public void setStatus(String status) {
        this.status = status;
        if ("RUNNING".equals(status) && startedAt == null) {
            this.startedAt = Instant.now();
        }
        if (("SUCCESS".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status)) && completedAt == null) {
            this.completedAt = Instant.now();
            if (startedAt != null) {
                this.durationMs = completedAt.toEpochMilli() - startedAt.toEpochMilli();
            }
        }
    }

    public void complete(int exitCode) {
        this.exitCode = exitCode;
        if (exitCode == 0) {
            setStatus("SUCCESS");
        } else {
            setStatus("FAILED");
            this.failureReason = "Job failed with non-zero exit code: " + exitCode;
        }
    }

    public void retry() {
        this.retryCount++;
        this.status = "RUNNING";
        this.startedAt = Instant.now();
        this.completedAt = null;
        this.exitCode = null;
        this.failureReason = null;
    }
}
