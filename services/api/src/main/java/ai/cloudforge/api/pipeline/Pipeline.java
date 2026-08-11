package ai.cloudforge.api.pipeline;

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
@Table(name = "pipelines")
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "repository_id")
    private UUID repositoryId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "yaml_definition", nullable = false, columnDefinition = "TEXT")
    private String yamlDefinition;

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Pipeline() {
    }

    public Pipeline(UUID projectId, UUID repositoryId, String name, String description, String yamlDefinition) {
        this.projectId = projectId;
        this.repositoryId = repositoryId;
        this.name = name;
        this.description = description;
        this.yamlDefinition = yamlDefinition;
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

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getYamlDefinition() {
        return yamlDefinition;
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

    public void updateDetails(String name, String description, String yamlDefinition) {
        this.name = name;
        this.description = description;
        this.yamlDefinition = yamlDefinition;
    }
}
