package ai.cloudforge.api.k8s;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class K8sDtos {

    public record ConnectClusterRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank String provider,
            @NotBlank String apiServerUrl,
            String kubeconfig,
            String environment
    ) {}

    public record ClusterResponseDto(
            UUID id,
            UUID projectId,
            UUID organizationId,
            String name,
            String provider,
            String apiServerUrl,
            String environment,
            String status,
            int runningPods,
            int totalNodes,
            double cpuUtilizationPct,
            double memoryUtilizationPct,
            Instant lastSyncedAt,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record DeployHelmReleaseRequest(
            @NotBlank @Size(max = 100) String releaseName,
            String namespace,
            @NotBlank String chartName,
            @NotBlank String chartVersion,
            String valuesYaml
    ) {}

    public record HelmReleaseDto(
            UUID id,
            UUID clusterId,
            UUID projectId,
            String releaseName,
            String namespace,
            String chartName,
            String chartVersion,
            String status,
            Instant deployedAt
    ) {}

    public record SyncGitOpsRequest(
            @NotBlank String repoUrl,
            String targetRevision,
            String path
    ) {}

    public record GitOpsSyncConfigDto(
            UUID id,
            UUID clusterId,
            UUID projectId,
            String repoUrl,
            String targetRevision,
            String path,
            String syncStatus,
            Instant lastSyncedAt
    ) {}

    public record K8sClusterSummaryDto(
            int activeClusters,
            String healthStatus,
            int runningPods,
            int evictions,
            double cpuUtilizationPct,
            int totalCpuCores,
            double memoryUtilizationPct,
            int totalMemoryGib,
            String lastRefreshedAt,
            List<K8sDeploymentSummaryDto> deployments
    ) {}

    public record K8sDeploymentSummaryDto(
            String id,
            String name,
            String strategy,
            String status,
            String namespace,
            int replicasReady,
            int replicasDesired
    ) {}
}
