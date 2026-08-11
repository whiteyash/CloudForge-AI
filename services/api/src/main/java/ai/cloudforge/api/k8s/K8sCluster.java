package ai.cloudforge.api.k8s;

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
@Table(name = "k8s_clusters")
public class K8sCluster {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "api_server_url", nullable = false)
    private String apiServerUrl;

    @Column(name = "encrypted_kubeconfig", columnDefinition = "TEXT")
    private String encryptedKubeconfig;

    @Column(name = "environment", nullable = false, length = 50)
    private String environment;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "running_pods", nullable = false)
    private int runningPods;

    @Column(name = "total_nodes", nullable = false)
    private int totalNodes;

    @Column(name = "cpu_utilization_pct", nullable = false)
    private double cpuUtilizationPct;

    @Column(name = "memory_utilization_pct", nullable = false)
    private double memoryUtilizationPct;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public K8sCluster() {}

    public K8sCluster(UUID projectId, UUID organizationId, String name, String provider, String apiServerUrl, String environment) {
        this.projectId = projectId;
        this.organizationId = organizationId;
        this.name = name;
        this.provider = provider;
        this.apiServerUrl = apiServerUrl;
        this.environment = environment != null ? environment : "PRODUCTION";
        this.status = "CONNECTED";
        this.runningPods = 0;
        this.totalNodes = 1;
        this.cpuUtilizationPct = 0.0;
        this.memoryUtilizationPct = 0.0;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.environment == null) this.environment = "PRODUCTION";
        if (this.status == null) this.status = "CONNECTED";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getApiServerUrl() { return apiServerUrl; }
    public void setApiServerUrl(String apiServerUrl) { this.apiServerUrl = apiServerUrl; }

    public String getEncryptedKubeconfig() { return encryptedKubeconfig; }
    public void setEncryptedKubeconfig(String encryptedKubeconfig) { this.encryptedKubeconfig = encryptedKubeconfig; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRunningPods() { return runningPods; }
    public void setRunningPods(int runningPods) { this.runningPods = runningPods; }

    public int getTotalNodes() { return totalNodes; }
    public void setTotalNodes(int totalNodes) { this.totalNodes = totalNodes; }

    public double getCpuUtilizationPct() { return cpuUtilizationPct; }
    public void setCpuUtilizationPct(double cpuUtilizationPct) { this.cpuUtilizationPct = cpuUtilizationPct; }

    public double getMemoryUtilizationPct() { return memoryUtilizationPct; }
    public void setMemoryUtilizationPct(double memoryUtilizationPct) { this.memoryUtilizationPct = memoryUtilizationPct; }

    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
