package ai.cloudforge.api.runner;

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
@Table(name = "runners")
public class Runner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "runner_type", nullable = false, length = 50)
    private String runnerType;

    @Column(name = "runner_group", length = 100)
    private String runnerGroup = "default";

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(length = 30)
    private String status = "ONLINE";

    @Column(length = 255)
    private String labels = "ubuntu-latest";

    @Column(name = "operating_system", length = 50)
    private String operatingSystem = "linux";

    @Column(name = "max_parallel_jobs")
    private Integer maxParallelJobs = 2;

    @Column(name = "current_jobs")
    private Integer currentJobs = 0;

    @Column(name = "last_heartbeat", nullable = false)
    private Instant lastHeartbeat;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Runner() {
    }

    public Runner(UUID projectId, String name, String runnerType, String runnerGroup, String tokenHash, String labels, String operatingSystem, Integer maxParallelJobs) {
        this.projectId = projectId;
        this.name = name;
        this.runnerType = runnerType;
        this.runnerGroup = runnerGroup != null ? runnerGroup : "default";
        this.tokenHash = tokenHash;
        this.labels = labels != null ? labels : "ubuntu-latest";
        this.operatingSystem = operatingSystem != null ? operatingSystem : "linux";
        this.maxParallelJobs = maxParallelJobs != null ? maxParallelJobs : 2;
        this.lastHeartbeat = Instant.now();
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

    public String getName() {
        return name;
    }

    public String getRunnerType() {
        return runnerType;
    }

    public String getRunnerGroup() {
        return runnerGroup;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getStatus() {
        return status;
    }

    public String getLabels() {
        return labels;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public Integer getMaxParallelJobs() {
        return maxParallelJobs;
    }

    public Integer getCurrentJobs() {
        return currentJobs;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = Instant.now();
        if ("OFFLINE".equals(this.status)) {
            this.status = "ONLINE";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void assignJob() {
        this.currentJobs++;
        if (this.currentJobs >= this.maxParallelJobs) {
            this.status = "BUSY";
        }
    }
}
