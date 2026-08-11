package ai.cloudforge.api.k8s;

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
@Table(name = "gitops_sync_configs")
public class GitOpsSyncConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cluster_id", nullable = false)
    private UUID clusterId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @Column(name = "target_revision", nullable = false, length = 100)
    private String targetRevision;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "sync_status", nullable = false, length = 50)
    private String syncStatus;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public GitOpsSyncConfig() {}

    public GitOpsSyncConfig(UUID clusterId, UUID projectId, String repoUrl, String targetRevision, String path) {
        this.clusterId = clusterId;
        this.projectId = projectId;
        this.repoUrl = repoUrl;
        this.targetRevision = targetRevision != null ? targetRevision : "main";
        this.path = path != null ? path : "/";
        this.syncStatus = "SYNCED";
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.targetRevision == null) this.targetRevision = "main";
        if (this.path == null) this.path = "/";
        if (this.syncStatus == null) this.syncStatus = "SYNCED";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClusterId() { return clusterId; }
    public void setClusterId(UUID clusterId) { this.clusterId = clusterId; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getTargetRevision() { return targetRevision; }
    public void setTargetRevision(String targetRevision) { this.targetRevision = targetRevision; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
