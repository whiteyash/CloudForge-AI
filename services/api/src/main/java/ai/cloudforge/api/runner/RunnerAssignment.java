package ai.cloudforge.api.runner;

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
@Table(name = "runner_assignments")
public class RunnerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "runner_id", nullable = false)
    private UUID runnerId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(length = 30)
    private String status = "ASSIGNED";

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    protected RunnerAssignment() {
    }

    public RunnerAssignment(UUID runnerId, UUID jobId) {
        this.runnerId = runnerId;
        this.jobId = jobId;
    }

    @PrePersist
    void onCreate() {
        if (assignedAt == null) {
            assignedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRunnerId() {
        return runnerId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }
}
