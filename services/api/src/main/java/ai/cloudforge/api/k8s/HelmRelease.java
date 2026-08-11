package ai.cloudforge.api.k8s;

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
@Table(name = "helm_releases")
public class HelmRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cluster_id", nullable = false)
    private UUID clusterId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "release_name", nullable = false, length = 100)
    private String releaseName;

    @Column(name = "namespace", nullable = false, length = 100)
    private String namespace;

    @Column(name = "chart_name", nullable = false, length = 100)
    private String chartName;

    @Column(name = "chart_version", nullable = false, length = 50)
    private String chartVersion;

    @Column(name = "values_yaml", columnDefinition = "TEXT")
    private String valuesYaml;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "deployed_at", nullable = false)
    private Instant deployedAt;

    public HelmRelease() {}

    public HelmRelease(UUID clusterId, UUID projectId, String releaseName, String namespace, String chartName, String chartVersion, String valuesYaml) {
        this.clusterId = clusterId;
        this.projectId = projectId;
        this.releaseName = releaseName;
        this.namespace = namespace != null ? namespace : "default";
        this.chartName = chartName;
        this.chartVersion = chartVersion;
        this.valuesYaml = valuesYaml;
        this.status = "DEPLOYED";
    }

    @PrePersist
    protected void onCreate() {
        if (this.deployedAt == null) {
            this.deployedAt = Instant.now();
        }
        if (this.status == null) {
            this.status = "DEPLOYED";
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClusterId() { return clusterId; }
    public void setClusterId(UUID clusterId) { this.clusterId = clusterId; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public String getReleaseName() { return releaseName; }
    public void setReleaseName(String releaseName) { this.releaseName = releaseName; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getChartName() { return chartName; }
    public void setChartName(String chartName) { this.chartName = chartName; }

    public String getChartVersion() { return chartVersion; }
    public void setChartVersion(String chartVersion) { this.chartVersion = chartVersion; }

    public String getValuesYaml() { return valuesYaml; }
    public void setValuesYaml(String valuesYaml) { this.valuesYaml = valuesYaml; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getDeployedAt() { return deployedAt; }
    public void setDeployedAt(Instant deployedAt) { this.deployedAt = deployedAt; }
}
