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
@Table(name = "knowledge_graph_nodes")
public class KnowledgeGraphNode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "node_name", nullable = false, length = 150)
    private String nodeName;

    @Column(name = "node_type", nullable = false, length = 50)
    private String nodeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected KnowledgeGraphNode() {
    }

    public KnowledgeGraphNode(UUID projectId, String nodeName, String nodeType) {
        this.projectId = projectId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
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

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
