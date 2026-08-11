package ai.cloudforge.api.ai.rca;

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
@Table(name = "dependency_links")
public class DependencyLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_component", nullable = false, length = 150)
    private String sourceComponent;

    @Column(name = "target_component", nullable = false, length = 150)
    private String targetComponent;

    @Column(name = "link_type", nullable = false, length = 50)
    private String linkType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected DependencyLink() {
    }

    public DependencyLink(UUID projectId, String sourceComponent, String targetComponent, String linkType) {
        this.projectId = projectId;
        this.sourceComponent = sourceComponent;
        this.targetComponent = targetComponent;
        this.linkType = linkType;
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

    public String getSourceComponent() {
        return sourceComponent;
    }

    public String getTargetComponent() {
        return targetComponent;
    }

    public String getLinkType() {
        return linkType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
