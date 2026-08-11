package ai.cloudforge.api.git;

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
@Table(name = "repository_webhooks")
public class RepositoryWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "connection_id")
    private UUID connectionId;

    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    @Column(name = "target_url", nullable = false, length = 255)
    private String targetUrl;

    @Column(nullable = false, length = 255)
    private String secret;

    @Column(length = 255)
    private String events = "push,pull_request";

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RepositoryWebhook() {
    }

    public RepositoryWebhook(UUID projectId, UUID connectionId, String providerName, String targetUrl, String secret, String events) {
        this.projectId = projectId;
        this.connectionId = connectionId;
        this.providerName = providerName;
        this.targetUrl = targetUrl;
        this.secret = secret;
        this.events = events != null ? events : "push,pull_request";
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

    public UUID getConnectionId() {
        return connectionId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public String getEvents() {
        return events;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
