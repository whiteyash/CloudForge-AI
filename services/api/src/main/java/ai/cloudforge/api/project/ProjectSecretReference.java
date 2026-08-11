package ai.cloudforge.api.project;

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
@Table(name = "project_secret_references")
public class ProjectSecretReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "secret_name", nullable = false, length = 100)
    private String secretName;

    @Column(name = "vault_path", nullable = false, length = 255)
    private String vaultPath;

    @Column(name = "vault_key", nullable = false, length = 100)
    private String vaultKey;

    @Column(length = 50)
    private String scope = "ALL_ENVIRONMENTS";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProjectSecretReference() {
    }

    public ProjectSecretReference(UUID projectId, String secretName, String vaultPath, String vaultKey, String scope) {
        this.projectId = projectId;
        this.secretName = secretName;
        this.vaultPath = vaultPath;
        this.vaultKey = vaultKey;
        this.scope = scope;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getSecretName() {
        return secretName;
    }

    public String getVaultPath() {
        return vaultPath;
    }

    public String getVaultKey() {
        return vaultKey;
    }

    public String getScope() {
        return scope;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
