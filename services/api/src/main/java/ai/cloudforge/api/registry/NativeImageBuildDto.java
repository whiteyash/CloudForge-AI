package ai.cloudforge.api.registry;

import java.time.Instant;
import java.util.UUID;

public class NativeImageBuildDto {

    private UUID id;
    private UUID projectId;
    private UUID registryId;
    private String repositoryName;
    private String tagName;
    private String dockerfilePath;
    private String status;
    private String logOutput;
    private Instant startedAt;
    private Instant completedAt;
    private Instant createdAt;

    public NativeImageBuildDto() {
    }

    public NativeImageBuildDto(NativeImageBuild build) {
        this.id = build.getId();
        this.projectId = build.getProjectId();
        this.registryId = build.getRegistryId();
        this.repositoryName = build.getRepositoryName();
        this.tagName = build.getTagName();
        this.dockerfilePath = build.getDockerfilePath();
        this.status = build.getStatus();
        this.logOutput = build.getLogOutput();
        this.startedAt = build.getStartedAt();
        this.completedAt = build.getCompletedAt();
        this.createdAt = build.getCreatedAt();
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
