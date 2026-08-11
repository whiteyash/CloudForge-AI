package ai.cloudforge.api.registry;

import java.time.Instant;
import java.util.UUID;

public class ContainerRegistryDto {

    private UUID id;
    private UUID projectId;
    private UUID organizationId;
    private String name;
    private String registryType;
    private String registryUrl;
    private String authType;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public ContainerRegistryDto() {
    }

    public ContainerRegistryDto(ContainerRegistry registry) {
        this.id = registry.getId();
        this.projectId = registry.getProjectId();
        this.organizationId = registry.getOrganizationId();
        this.name = registry.getName();
        this.registryType = registry.getRegistryType();
        this.registryUrl = registry.getRegistryUrl();
        this.authType = registry.getAuthType();
        this.status = registry.getStatus();
        this.createdAt = registry.getCreatedAt();
        this.updatedAt = registry.getUpdatedAt();
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

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
