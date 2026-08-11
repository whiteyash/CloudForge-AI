package ai.cloudforge.api.k8s;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.k8s.K8sDtos.ConnectClusterRequest;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

class K8sClusterSecurityTest {

    @Mock private K8sClusterRepository clusterRepository;
    @Mock private HelmReleaseRepository helmReleaseRepository;
    @Mock private GitOpsSyncConfigRepository gitOpsSyncConfigRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private RbacService rbacService;

    private K8sClusterService service;
    private UUID projectId;
    private UUID orgId;
    private UUID unauthorizedUserId;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new K8sClusterService(
                clusterRepository,
                helmReleaseRepository,
                gitOpsSyncConfigRepository,
                projectRepository,
                rbacService,
                "TestSecurityEncryptionSecretKey32B!"
        );

        unauthorizedUserId = UUID.randomUUID();

        Organization org = new Organization("Org A", "org-a");
        orgId = org.getId();

        project = new Project(org, "Project A", "https://github.com/org-a/repo-a", "proj-a");
        projectId = project.getId();
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user lacks K8S_MANAGE permission")
    void connectCluster_Forbidden() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doThrow(new ForbiddenException("User lacks permission K8S_MANAGE"))
                .when(rbacService).requirePermission(unauthorizedUserId, orgId, "K8S_MANAGE");

        ConnectClusterRequest request = new ConnectClusterRequest("unauth-cluster", "GKE", "https://gke.unauth.com", null, "PRODUCTION");

        assertThrows(ForbiddenException.class, () -> service.connectCluster(projectId, request, unauthorizedUserId));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is unauthenticated (null userId)")
    void listClusters_Unauthenticated_Forbidden() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(ForbiddenException.class, () -> service.listClusters(projectId, null));
    }
}
