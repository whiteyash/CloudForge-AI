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
@Table(name = "causal_graphs")
public class CausalGraph {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "root_cause_report_id")
    private UUID rootCauseReportId;

    @Column(name = "graph_json", nullable = false, columnDefinition = "TEXT")
    private String graphJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CausalGraph() {
    }

    public CausalGraph(UUID projectId, UUID rootCauseReportId, String graphJson) {
        this.projectId = projectId;
        this.rootCauseReportId = rootCauseReportId;
        this.graphJson = graphJson;
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

    public UUID getRootCauseReportId() {
        return rootCauseReportId;
    }

    public String getGraphJson() {
        return graphJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
