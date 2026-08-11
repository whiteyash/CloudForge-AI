package ai.cloudforge.api.registry;

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
@Table(name = "container_registries")
public class ContainerRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private String name;

    @Column(name = "registry_type", nullable = false, length = 50)
    private String registryType; // DOCKER_HUB, AWS_ECR, GOOGLE_GAR, GITHUB_GHCR, AZURE_ACR, HARBOR_PRIVATE

    @Column(name = "registry_url", nullable = false, length = 512)
    private String registryUrl;

    @Column(name = "auth_type", nullable = false, length = 50)
    private String authType; // TOKEN, USERNAME_PASSWORD, AWS_IAM, SERVICE_ACCOUNT

    @Column(name = "encrypted_credentials", columnDefinition = "TEXT")
    private String encryptedCredentials;

    @Column(nullable = false, length = 30)
    private String status = "CONNECTED"; // CONNECTED, UNREACHABLE, INVALID_CREDENTIALS, UNCONFIGURED

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ContainerRegistry() {
    }

    public ContainerRegistry(UUID projectId, UUID organizationId, String name, String registryType, String registryUrl, String authType, String encryptedCredentials) {
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.registryType = registryType;
        this.registryUrl = registryUrl;
        this.authType = authType;
        this.encryptedCredentials = encryptedCredentials;
        this.status = "CONNECTED";
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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

    public String getEncryptedCredentials() {
        return encryptedCredentials;
    }

    public void setEncryptedCredentials(String encryptedCredentials) {
        this.encryptedCredentials = encryptedCredentials;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
