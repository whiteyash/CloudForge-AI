package ai.cloudforge.api.ai.log;

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
@Table(name = "log_clusters")
public class LogCluster {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "cluster_name", nullable = false, length = 150)
    private String clusterName;

    @Column(length = 30)
    private String severity = "ERROR";

    @Column(name = "occurrence_count")
    private int occurrenceCount = 1;

    @Column(name = "affected_services", nullable = false, length = 255)
    private String affectedServices;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LogCluster() {
    }

    public LogCluster(UUID projectId, String clusterName, String severity, int occurrenceCount, String affectedServices) {
        this.projectId = projectId;
        this.clusterName = clusterName;
        this.severity = severity != null ? severity : "ERROR";
        this.occurrenceCount = occurrenceCount;
        this.affectedServices = affectedServices;
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

    public String getClusterName() {
        return clusterName;
    }

    public String getSeverity() {
        return severity;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public String getAffectedServices() {
        return affectedServices;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
