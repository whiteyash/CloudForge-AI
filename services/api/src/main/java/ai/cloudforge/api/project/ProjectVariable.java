package ai.cloudforge.api.project;

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
@Table(name = "project_variables")
public class ProjectVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "environment_id")
    private UUID environmentId;

    @Column(name = "var_key", nullable = false, length = 100)
    private String key;

    @Column(name = "var_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "is_masked")
    private boolean isMasked = false;

    @Column(name = "is_protected")
    private boolean isProtected = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProjectVariable() {
    }

    public ProjectVariable(UUID projectId, UUID environmentId, String key, String value, boolean isMasked, boolean isProtected) {
        this.projectId = projectId;
        this.environmentId = environmentId;
        this.key = key;
        this.value = value;
        this.isMasked = isMasked;
        this.isProtected = isProtected;
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

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isMasked() {
        return isMasked;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
