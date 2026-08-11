package ai.cloudforge.api.artifact;

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
@Table(name = "artifacts")
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "pipeline_run_id")
    private UUID pipelineRunId;

    @Column(name = "job_id")
    private UUID jobId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "artifact_type", nullable = false, length = 50)
    private String artifactType;

    @Column(length = 50)
    private String version = "1.0.0";

    @Column(name = "sha256_checksum", nullable = false, length = 64)
    private String sha256Checksum;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "mime_type", length = 100)
    private String mimeType = "application/octet-stream";

    @Column(name = "storage_provider", length = 50)
    private String storageProvider = "LOCAL";

    @Column(name = "storage_key", nullable = false, length = 255)
    private String storageKey;

    @Column(name = "retention_status", length = 30)
    private String retentionStatus = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Artifact() {
    }

    public Artifact(UUID projectId, UUID pipelineRunId, UUID jobId, String name, String artifactType, String version, String sha256Checksum, Long sizeBytes, String mimeType, String storageProvider, String storageKey) {
        this.projectId = projectId;
        this.pipelineRunId = pipelineRunId;
        this.jobId = jobId;
        this.name = name;
        this.artifactType = artifactType;
        this.version = version != null ? version : "1.0.0";
        this.sha256Checksum = sha256Checksum;
        this.sizeBytes = sizeBytes;
        this.mimeType = mimeType != null ? mimeType : "application/octet-stream";
        this.storageProvider = storageProvider != null ? storageProvider : "LOCAL";
        this.storageKey = storageKey;
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

    public UUID getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public String getVersion() {
        return version;
    }

    public String getSha256Checksum() {
        return sha256Checksum;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getRetentionStatus() {
        return retentionStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setRetentionStatus(String retentionStatus) {
        this.retentionStatus = retentionStatus;
    }
}
