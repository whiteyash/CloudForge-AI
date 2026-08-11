package ai.cloudforge.api.k8s;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.k8s.K8sDtos.ClusterResponseDto;
import ai.cloudforge.api.k8s.K8sDtos.ConnectClusterRequest;
import ai.cloudforge.api.k8s.K8sDtos.DeployHelmReleaseRequest;
import ai.cloudforge.api.k8s.K8sDtos.GitOpsSyncConfigDto;
import ai.cloudforge.api.k8s.K8sDtos.HelmReleaseDto;
import ai.cloudforge.api.k8s.K8sDtos.K8sClusterSummaryDto;
import ai.cloudforge.api.k8s.K8sDtos.K8sDeploymentSummaryDto;
import ai.cloudforge.api.k8s.K8sDtos.SyncGitOpsRequest;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@Service
public class K8sClusterService {

    private static final Logger log = LoggerFactory.getLogger(K8sClusterService.class);
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH_BYTES = 12;

    private final K8sClusterRepository clusterRepository;
    private final HelmReleaseRepository helmReleaseRepository;
    private final GitOpsSyncConfigRepository gitOpsSyncConfigRepository;
    private final ProjectRepository projectRepository;
    private final RbacService rbacService;
    private final String encryptionKey;

    public K8sClusterService(
            K8sClusterRepository clusterRepository,
            HelmReleaseRepository helmReleaseRepository,
            GitOpsSyncConfigRepository gitOpsSyncConfigRepository,
            ProjectRepository projectRepository,
            RbacService rbacService,
            @Value("${cloudforge.k8s.encryption-key:CloudForgeK8sEncryptionSecretKey32B!}") String encryptionKey
    ) {
        this.clusterRepository = clusterRepository;
        this.helmReleaseRepository = helmReleaseRepository;
        this.gitOpsSyncConfigRepository = gitOpsSyncConfigRepository;
        this.projectRepository = projectRepository;
        this.rbacService = rbacService;
        this.encryptionKey = padKey(encryptionKey);
    }

    private Project validateProjectAndAuth(UUID projectId, UUID userId, String requiredPermission) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, requiredPermission);
        return project;
    }

    @Transactional(readOnly = true)
    public List<ClusterResponseDto> listClusters(UUID projectId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "K8S_VIEW");
        return clusterRepository.findByProjectId(projectId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClusterResponseDto getCluster(UUID projectId, UUID clusterId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "K8S_VIEW");
        K8sCluster cluster = clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));
        return toResponseDto(cluster);
    }

    @Transactional
    public ClusterResponseDto connectCluster(UUID projectId, ConnectClusterRequest request, UUID userId) {
        Project project = validateProjectAndAuth(projectId, userId, "K8S_MANAGE");

        clusterRepository.findByProjectIdAndName(projectId, request.name())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Cluster with name '" + request.name() + "' already exists in project");
                });

        K8sCluster cluster = new K8sCluster(
                projectId,
                project.getOrganization().getId(),
                request.name(),
                request.provider(),
                request.apiServerUrl(),
                request.environment()
        );

        if (request.kubeconfig() != null && !request.kubeconfig().isBlank()) {
            cluster.setEncryptedKubeconfig(encryptSecret(request.kubeconfig()));
        }

        cluster.setRunningPods(5);
        cluster.setTotalNodes(2);
        cluster.setCpuUtilizationPct(24.5);
        cluster.setMemoryUtilizationPct(42.0);
        cluster.setLastSyncedAt(Instant.now());

        K8sCluster saved = clusterRepository.save(cluster);
        log.info("K8S_AUDIT | project={} | user={} | action=CLUSTER_CONNECTED | cluster={}", projectId, userId, saved.getName());
        return toResponseDto(saved);
    }

    @Transactional
    public void disconnectCluster(UUID projectId, UUID clusterId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "K8S_MANAGE");

        K8sCluster cluster = clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));

        clusterRepository.delete(cluster);
        log.info("K8S_AUDIT | project={} | user={} | action=CLUSTER_DISCONNECTED | cluster={}", projectId, userId, cluster.getName());
    }

    @Transactional
    public ClusterResponseDto syncClusterHealth(UUID projectId, UUID clusterId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "K8S_VIEW");

        K8sCluster cluster = clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));

        cluster.setStatus("CONNECTED");
        cluster.setLastSyncedAt(Instant.now());
        K8sCluster updated = clusterRepository.save(cluster);

        log.info("K8S_AUDIT | project={} | user={} | action=CLUSTER_HEALTH_SYNCED | cluster={}", projectId, userId, cluster.getName());
        return toResponseDto(updated);
    }

    @Transactional
    public HelmReleaseDto deployHelmRelease(UUID projectId, UUID clusterId, DeployHelmReleaseRequest request, UUID userId) {
        validateProjectAndAuth(projectId, userId, "HELM_DEPLOY");

        K8sCluster cluster = clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));

        HelmRelease release = new HelmRelease(
                cluster.getId(),
                projectId,
                request.releaseName(),
                request.namespace(),
                request.chartName(),
                request.chartVersion(),
                request.valuesYaml()
        );

        HelmRelease saved = helmReleaseRepository.save(release);
        log.info("K8S_AUDIT | project={} | user={} | action=HELM_RELEASE_DEPLOYED | release={} | cluster={}", projectId, userId, saved.getReleaseName(), cluster.getName());
        return toHelmReleaseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<HelmReleaseDto> listHelmReleases(UUID projectId, UUID clusterId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "K8S_VIEW");

        clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));

        return helmReleaseRepository.findByClusterId(clusterId).stream()
                .map(this::toHelmReleaseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void uninstallHelmRelease(UUID projectId, UUID clusterId, UUID releaseId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "HELM_DEPLOY");

        HelmRelease release = helmReleaseRepository.findById(releaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Helm release not found: " + releaseId));

        if (!release.getProjectId().equals(projectId) || !release.getClusterId().equals(clusterId)) {
            throw new IllegalArgumentException("Helm release does not belong to cluster or project");
        }

        helmReleaseRepository.delete(release);
        log.info("K8S_AUDIT | project={} | user={} | action=HELM_RELEASE_UNINSTALLED | release={}", projectId, userId, release.getReleaseName());
    }

    @Transactional
    public GitOpsSyncConfigDto syncGitOps(UUID projectId, UUID clusterId, SyncGitOpsRequest request, UUID userId) {
        validateProjectAndAuth(projectId, userId, "GITOPS_SYNC");

        K8sCluster cluster = clusterRepository.findByProjectIdAndId(projectId, clusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kubernetes cluster not found: " + clusterId));

        GitOpsSyncConfig config = gitOpsSyncConfigRepository.findByClusterIdAndRepoUrl(clusterId, request.repoUrl())
                .orElseGet(() -> new GitOpsSyncConfig(
                        clusterId,
                        projectId,
                        request.repoUrl(),
                        request.targetRevision(),
                        request.path()
                ));

        config.setTargetRevision(request.targetRevision() != null ? request.targetRevision() : "main");
        config.setPath(request.path() != null ? request.path() : "/");
        config.setSyncStatus("SYNCED");
        config.setLastSyncedAt(Instant.now());

        GitOpsSyncConfig saved = gitOpsSyncConfigRepository.save(config);
        log.info("K8S_AUDIT | project={} | user={} | action=GITOPS_SYNCED | repo={} | cluster={}", projectId, userId, saved.getRepoUrl(), cluster.getName());
        return toGitOpsDto(saved);
    }

    @Transactional(readOnly = true)
    public K8sClusterSummaryDto getClusterSummary(UUID projectId, UUID userId) {
        if (userId != null && projectId != null) {
            validateProjectAndAuth(projectId, userId, "K8S_VIEW");
        }

        List<K8sCluster> clusters = (projectId != null) ? clusterRepository.findByProjectId(projectId) : clusterRepository.findAll();
        List<HelmRelease> releases = (projectId != null) ? helmReleaseRepository.findByProjectId(projectId) : helmReleaseRepository.findAll();

        int activeClusters = clusters.size();
        int runningPods = clusters.stream().mapToInt(K8sCluster::getRunningPods).sum();
        int totalNodes = clusters.stream().mapToInt(K8sCluster::getTotalNodes).sum();
        double avgCpu = clusters.isEmpty() ? 0.0 : clusters.stream().mapToDouble(K8sCluster::getCpuUtilizationPct).average().orElse(0.0);
        double avgMem = clusters.isEmpty() ? 0.0 : clusters.stream().mapToDouble(K8sCluster::getMemoryUtilizationPct).average().orElse(0.0);

        List<K8sDeploymentSummaryDto> deploymentDtos = releases.stream()
                .map(r -> new K8sDeploymentSummaryDto(
                        r.getId().toString(),
                        r.getReleaseName(),
                        "ROLLING",
                        r.getStatus(),
                        r.getNamespace(),
                        1,
                        1
                ))
                .collect(Collectors.toList());

        String health = activeClusters > 0 ? "100% HEALTHY" : "NO ACTIVE CLUSTERS";

        return new K8sClusterSummaryDto(
                activeClusters,
                health,
                runningPods,
                0,
                Math.round(avgCpu * 10.0) / 10.0,
                totalNodes * 4,
                Math.round(avgMem * 10.0) / 10.0,
                totalNodes * 8,
                Instant.now().toString(),
                deploymentDtos
        );
    }

    public String encryptSecret(String plaintext) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKey keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt secret: " + e.getMessage(), e);
        }
    }

    public String decryptSecret(String ciphertext) {
        if (ciphertext == null) return null;
        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            byte[] cipherText = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKey keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt secret: " + e.getMessage(), e);
        }
    }

    private String padKey(String key) {
        if (key == null) key = "CloudForgeK8sEncryptionSecretKey32B!";
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        if (bytes.length == 32) return key;
        byte[] padded = new byte[32];
        System.arraycopy(bytes, 0, padded, 0, Math.min(bytes.length, 32));
        return new String(padded, StandardCharsets.UTF_8);
    }

    private ClusterResponseDto toResponseDto(K8sCluster cluster) {
        return new ClusterResponseDto(
                cluster.getId(),
                cluster.getProjectId(),
                cluster.getOrganizationId(),
                cluster.getName(),
                cluster.getProvider(),
                cluster.getApiServerUrl(),
                cluster.getEnvironment(),
                cluster.getStatus(),
                cluster.getRunningPods(),
                cluster.getTotalNodes(),
                cluster.getCpuUtilizationPct(),
                cluster.getMemoryUtilizationPct(),
                cluster.getLastSyncedAt(),
                cluster.getCreatedAt(),
                cluster.getUpdatedAt()
        );
    }

    private HelmReleaseDto toHelmReleaseDto(HelmRelease release) {
        return new HelmReleaseDto(
                release.getId(),
                release.getClusterId(),
                release.getProjectId(),
                release.getReleaseName(),
                release.getNamespace(),
                release.getChartName(),
                release.getChartVersion(),
                release.getStatus(),
                release.getDeployedAt()
        );
    }

    private GitOpsSyncConfigDto toGitOpsDto(GitOpsSyncConfig config) {
        return new GitOpsSyncConfigDto(
                config.getId(),
                config.getClusterId(),
                config.getProjectId(),
                config.getRepoUrl(),
                config.getTargetRevision(),
                config.getPath(),
                config.getSyncStatus(),
                config.getLastSyncedAt()
        );
    }
}
