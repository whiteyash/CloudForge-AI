package ai.cloudforge.api.registry;

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
@Table(name = "native_image_builds")
public class NativeImageBuild {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "registry_id", nullable = false)
    private UUID registryId;

    @Column(name = "repository_name", nullable = false)
    private String repositoryName;

    @Column(name = "tag_name", nullable = false, length = 128)
    private String tagName;

    @Column(name = "dockerfile_path", nullable = false)
    private String dockerfilePath = "Dockerfile";

    @Column(nullable = false, length = 30)
    private String status = "QUEUED"; // QUEUED, BUILDING, PUSHED, FAILED, CANCELLED

    @Column(name = "log_output", columnDefinition = "TEXT")
    private String logOutput;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public NativeImageBuild() {
    }

    public NativeImageBuild(UUID projectId, UUID registryId, String repositoryName, String tagName, String dockerfilePath) {
        this.projectId = projectId;
        this.registryId = registryId;
        this.repositoryName = repositoryName;
        this.tagName = tagName;
        this.dockerfilePath = (dockerfilePath != null && !dockerfilePath.isBlank()) ? dockerfilePath : "Dockerfile";
        this.status = "QUEUED";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getRegistryId() {
        return registryId;
    }

    public void setRegistryId(UUID registryId) {
        this.registryId = registryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDockerfilePath() {
        return dockerfilePath;
    }

    public void setDockerfilePath(String dockerfilePath) {
        this.dockerfilePath = dockerfilePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogOutput() {
        return logOutput;
    }

    public void setLogOutput(String logOutput) {
        this.logOutput = logOutput;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
