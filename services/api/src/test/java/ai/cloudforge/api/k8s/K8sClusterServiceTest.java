package ai.cloudforge.api.k8s;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.k8s.K8sDtos.ClusterResponseDto;
import ai.cloudforge.api.k8s.K8sDtos.ConnectClusterRequest;
import ai.cloudforge.api.k8s.K8sDtos.DeployHelmReleaseRequest;
import ai.cloudforge.api.k8s.K8sDtos.GitOpsSyncConfigDto;
import ai.cloudforge.api.k8s.K8sDtos.HelmReleaseDto;
import ai.cloudforge.api.k8s.K8sDtos.SyncGitOpsRequest;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class K8sClusterServiceTest {

    @Mock private K8sClusterRepository clusterRepository;
    @Mock private HelmReleaseRepository helmReleaseRepository;
    @Mock private GitOpsSyncConfigRepository gitOpsSyncConfigRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private RbacService rbacService;

    private K8sClusterService clusterService;
    private UUID projectId;
    private UUID orgId;
    private UUID userId;
    private Project project;

    @BeforeEach
    void setUp() {
        clusterService = new K8sClusterService(
                clusterRepository,
                helmReleaseRepository,
                gitOpsSyncConfigRepository,
                projectRepository,
                rbacService,
                "TestSecretEncryptionKey32BLongValue!"
        );
        projectId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();

        Organization org = new Organization("CloudForge Corp", "cloudforge-corp");
        orgId = org.getId();

        project = new Project(org, "K8s Demo Project", "https://github.com/cloudforge/k8s-demo", "k8s-demo");
        projectId = project.getId();
    }

    @Test
    @DisplayName("Should connect cluster successfully and encrypt kubeconfig")
    void connectCluster_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "K8S_MANAGE");
        when(clusterRepository.findByProjectIdAndName(eq(projectId), any())).thenReturn(Optional.empty());
        when(clusterRepository.save(any(K8sCluster.class))).thenAnswer(i -> {
            K8sCluster c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ConnectClusterRequest request = new ConnectClusterRequest("prod-us-east", "EKS", "https://k8s.prod.us-east.eks.com", "apiVersion: v1", "PRODUCTION");
        ClusterResponseDto response = clusterService.connectCluster(projectId, request, userId);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("prod-us-east");
        assertThat(response.provider()).isEqualTo("EKS");
        assertThat(response.status()).isEqualTo("CONNECTED");

        verify(clusterRepository).save(any(K8sCluster.class));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is unauthenticated")
    void connectCluster_Unauthenticated() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ConnectClusterRequest request = new ConnectClusterRequest("prod-us-east", "EKS", "https://k8s.prod.us-east.eks.com", "apiVersion: v1", "PRODUCTION");
        assertThatThrownBy(() -> clusterService.connectCluster(projectId, request, null))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Unauthenticated request");
    }

    @Test
    @DisplayName("Should deploy Helm release successfully")
    void deployHelmRelease_Success() {
        UUID clusterId = UUID.randomUUID();
        K8sCluster cluster = new K8sCluster(projectId, orgId, "staging-k8s", "GKE", "https://gke.staging.internal", "STAGING");
        cluster.setId(clusterId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "HELM_DEPLOY");
        when(clusterRepository.findByProjectIdAndId(projectId, clusterId)).thenReturn(Optional.of(cluster));
        when(helmReleaseRepository.save(any(HelmRelease.class))).thenAnswer(i -> {
            HelmRelease r = i.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        DeployHelmReleaseRequest request = new DeployHelmReleaseRequest("app-gateway", "staging", "ingress-nginx", "4.0.1", "replicaCount: 2");
        HelmReleaseDto response = clusterService.deployHelmRelease(projectId, clusterId, request, userId);

        assertThat(response).isNotNull();
        assertThat(response.releaseName()).isEqualTo("app-gateway");
        assertThat(response.status()).isEqualTo("DEPLOYED");
    }

    @Test
    @DisplayName("Should sync GitOps configuration successfully")
    void syncGitOps_Success() {
        UUID clusterId = UUID.randomUUID();
        K8sCluster cluster = new K8sCluster(projectId, orgId, "prod-k8s", "AKS", "https://aks.prod.azure.com", "PRODUCTION");
        cluster.setId(clusterId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "GITOPS_SYNC");
        when(clusterRepository.findByProjectIdAndId(projectId, clusterId)).thenReturn(Optional.of(cluster));
        when(gitOpsSyncConfigRepository.findByClusterIdAndRepoUrl(eq(clusterId), any())).thenReturn(Optional.empty());
        when(gitOpsSyncConfigRepository.save(any(GitOpsSyncConfig.class))).thenAnswer(i -> {
            GitOpsSyncConfig g = i.getArgument(0);
            g.setId(UUID.randomUUID());
            return g;
        });

        SyncGitOpsRequest request = new SyncGitOpsRequest("https://github.com/cloudforge/k8s-manifests.git", "main", "/environments/production");
        GitOpsSyncConfigDto response = clusterService.syncGitOps(projectId, clusterId, request, userId);

        assertThat(response).isNotNull();
        assertThat(response.repoUrl()).isEqualTo("https://github.com/cloudforge/k8s-manifests.git");
        assertThat(response.syncStatus()).isEqualTo("SYNCED");
    }

    @Test
    @DisplayName("Should encrypt and decrypt secret symmetrically")
    void encryptAndDecryptSecret_Success() {
        String secret = "apiVersion: v1\nkind: Config\nclusters: []";
        String cipher = clusterService.encryptSecret(secret);
        assertThat(cipher).isNotNull().isNotEqualTo(secret);

        String decrypted = clusterService.decryptSecret(cipher);
        assertThat(decrypted).isEqualTo(secret);
    }
}
