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
@Table(name = "project_environments")
public class ProjectEnvironment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String type = "DEV";

    @Column(name = "k8s_namespace", length = 100)
    private String k8sNamespace;

    @Column(name = "cluster_name", length = 100)
    private String clusterName = "primary-cluster";

    @Column(name = "auto_deploy")
    private boolean autoDeploy = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProjectEnvironment() {
    }

    public ProjectEnvironment(UUID projectId, String name, String type, String k8sNamespace) {
        this.projectId = projectId;
        this.name = name;
        this.type = type;
        this.k8sNamespace = k8sNamespace;
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

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getK8sNamespace() {
        return k8sNamespace;
    }

    public String getClusterName() {
        return clusterName;
    }

    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
