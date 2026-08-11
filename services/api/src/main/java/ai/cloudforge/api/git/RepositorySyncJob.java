package ai.cloudforge.api.git;

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
@Table(name = "repository_sync_jobs")
public class RepositorySyncJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "job_type", length = 50)
    private String jobType = "MANUAL";

    @Column(length = 30)
    private String status = "COMPLETED";

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    protected RepositorySyncJob() {
    }

    public RepositorySyncJob(UUID repositoryId, String jobType, String status) {
        this.repositoryId = repositoryId;
        this.jobType = jobType;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        if (startedAt == null) {
            startedAt = Instant.now();
        }
        if (completedAt == null && "COMPLETED".equals(status)) {
            completedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getJobType() {
        return jobType;
    }

    public String getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
