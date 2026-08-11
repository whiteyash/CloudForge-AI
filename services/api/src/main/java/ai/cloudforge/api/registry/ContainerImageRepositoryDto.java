package ai.cloudforge.api.registry;

import java.time.Instant;
import java.util.UUID;

public class ContainerImageRepositoryDto {

    private UUID id;
    private UUID registryId;
    private UUID projectId;
    private String repositoryName;
    private Integer imageCount;
    private Integer pullCount;
    private Instant createdAt;
    private Instant updatedAt;

    public ContainerImageRepositoryDto() {
    }

    public ContainerImageRepositoryDto(ContainerImageRepository repo) {
        this.id = repo.getId();
        this.registryId = repo.getRegistryId();
        this.projectId = repo.getProjectId();
        this.repositoryName = repo.getRepositoryName();
        this.imageCount = repo.getImageCount();
        this.pullCount = repo.getPullCount();
        this.createdAt = repo.getCreatedAt();
        this.updatedAt = repo.getUpdatedAt();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRegistryId() {
        return registryId;
    }

    public void setRegistryId(UUID registryId) {
        this.registryId = registryId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public Integer getPullCount() {
        return pullCount;
    }

    public void setPullCount(Integer pullCount) {
        this.pullCount = pullCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
