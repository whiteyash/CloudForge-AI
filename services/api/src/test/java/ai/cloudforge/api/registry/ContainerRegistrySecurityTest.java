package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;
import ai.cloudforge.api.registry.provider.ContainerRegistryProviderFactory;
import ai.cloudforge.api.registry.provider.GenericOciRegistryProvider;

class ContainerRegistrySecurityTest {

    @Mock
    private ContainerRegistryRepository registryRepository;

    @Mock
    private ContainerImageRepositoryRepository imageRepositoryRepository;

    @Mock
    private ContainerImageTagRepository imageTagRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RbacService rbacService;

    private ContainerRegistryService service;

    private UUID projectId;
    private UUID unauthorizedUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        GenericOciRegistryProvider genericProvider = new GenericOciRegistryProvider();
        ContainerRegistryProviderFactory providerFactory = new ContainerRegistryProviderFactory(List.of(genericProvider), genericProvider);

        service = new ContainerRegistryService(
                registryRepository,
                imageRepositoryRepository,
                imageTagRepository,
                projectRepository,
                rbacService,
                providerFactory
        );

        projectId = UUID.randomUUID();
        unauthorizedUserId = UUID.randomUUID();
    }

    @Test
    void connectRegistry_CrossTenantUser_ThrowsForbiddenException() {
        Organization org = new Organization("Org B", "org-b");
        Project project = new Project(org, "Org B Project", "https://github.com/org-b/repo", "prod-b");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        doThrow(new ForbiddenException("Access denied: Not a member of this organization"))
                .when(rbacService).requirePermission(eq(unauthorizedUserId), any(), eq("REGISTRY_MANAGE"));

        CreateRegistryRequest request = new CreateRegistryRequest();
        request.setName("Rogue Registry");
        request.setRegistryType("AWS_ECR");
        request.setRegistryUrl("https://123456789.dkr.ecr.us-east-1.amazonaws.com");
        request.setAuthType("AWS_IAM");

        assertThrows(ForbiddenException.class, () -> service.connectRegistry(projectId, request, unauthorizedUserId));
    }

    @Test
    void deleteTag_MissingImageDeletePermission_ThrowsForbiddenException() {
        Organization org = new Organization("Org A", "org-a");
        Project project = new Project(org, "Org A Project", "https://github.com/org-a/repo", "prod-a");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        doThrow(new ForbiddenException("Access denied: Missing required permission IMAGE_DELETE"))
                .when(rbacService).requirePermission(eq(unauthorizedUserId), any(), eq("IMAGE_DELETE"));

        UUID registryId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();

        assertThrows(ForbiddenException.class, () -> service.deleteImageTag(projectId, registryId, repoId, tagId, unauthorizedUserId));
    }
}
