package ai.cloudforge.api.ai.knowledge;

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
@Table(name = "knowledge_graph_edges")
public class KnowledgeGraphEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_node_id")
    private UUID sourceNodeId;

    @Column(name = "target_node_id")
    private UUID targetNodeId;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected KnowledgeGraphEdge() {
    }

    public KnowledgeGraphEdge(UUID projectId, UUID sourceNodeId, UUID targetNodeId, String relationshipType) {
        this.projectId = projectId;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.relationshipType = relationshipType;
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

    public UUID getSourceNodeId() {
        return sourceNodeId;
    }

    public UUID getTargetNodeId() {
        return targetNodeId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
