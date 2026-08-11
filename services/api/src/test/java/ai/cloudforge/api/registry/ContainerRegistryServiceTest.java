package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;
import ai.cloudforge.api.registry.provider.ContainerRegistryProviderFactory;
import ai.cloudforge.api.registry.provider.GenericOciRegistryProvider;

class ContainerRegistryServiceTest {

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
    private UUID orgId;
    private UUID userId;

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
        orgId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void connectRegistry_Success() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(registryRepository.existsByProjectIdAndName(projectId, "Production ECR")).thenReturn(false);

        CreateRegistryRequest request = new CreateRegistryRequest();
        request.setName("Production ECR");
        request.setRegistryType("AWS_ECR");
        request.setRegistryUrl("https://123456789.dkr.ecr.us-east-1.amazonaws.com");
        request.setAuthType("AWS_IAM");
        request.setCredentials("accessKey:secretKey");

        ContainerRegistry saved = new ContainerRegistry(projectId, orgId, "Production ECR", "AWS_ECR", request.getRegistryUrl(), "AWS_IAM", "encrypted");
        saved.setId(UUID.randomUUID());

        when(registryRepository.save(any())).thenReturn(saved);

        ContainerRegistryDto dto = service.connectRegistry(projectId, request, userId);

        assertNotNull(dto);
        assertEquals("Production ECR", dto.getName());
        assertEquals("AWS_ECR", dto.getRegistryType());
    }

    @Test
    void connectRegistry_DuplicateName_ThrowsIllegalArgumentException() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(registryRepository.existsByProjectIdAndName(projectId, "Production ECR")).thenReturn(true);

        CreateRegistryRequest request = new CreateRegistryRequest();
        request.setName("Production ECR");
        request.setRegistryType("AWS_ECR");
        request.setRegistryUrl("https://123456789.dkr.ecr.us-east-1.amazonaws.com");
        request.setAuthType("AWS_IAM");

        assertThrows(IllegalArgumentException.class, () -> service.connectRegistry(projectId, request, userId));
    }

    @Test
    void testConnection_UnreachableHost_ThrowsIllegalArgumentException() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        UUID registryId = UUID.randomUUID();
        ContainerRegistry registry = new ContainerRegistry(projectId, orgId, "Docker Hub", "DOCKER_HUB", "https://invalid-registry-url-123456.local", "TOKEN", "encryptedKey");
        registry.setId(registryId);

        when(registryRepository.findByIdAndProjectId(registryId, projectId)).thenReturn(Optional.of(registry));
        when(registryRepository.save(any())).thenReturn(registry);

        assertThrows(IllegalArgumentException.class, () -> service.testConnection(projectId, registryId, userId));
    }

    @Test
    void deleteImageTag_ImmutableTag_ThrowsIllegalArgumentException() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        UUID registryId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();

        ContainerRegistry registry = new ContainerRegistry(projectId, orgId, "Docker Hub", "DOCKER_HUB", "https://index.docker.io/v1/", "TOKEN", "encryptedKey");
        registry.setId(registryId);

        ContainerImageRepository repo = new ContainerImageRepository(registryId, projectId, "myorg/api");
        repo.setId(repoId);

        ContainerImageTag tag = new ContainerImageTag(repoId, "v1.0.0", "sha256:abc", 1000L, "linux/amd64", true);
        tag.setId(tagId);

        when(registryRepository.findByIdAndProjectId(registryId, projectId)).thenReturn(Optional.of(registry));
        when(imageRepositoryRepository.findByIdAndRegistryId(repoId, registryId)).thenReturn(Optional.of(repo));
        when(imageTagRepository.findByIdAndRepositoryId(tagId, repoId)).thenReturn(Optional.of(tag));

        assertThrows(IllegalArgumentException.class, () -> service.deleteImageTag(projectId, registryId, repoId, tagId, userId));
    }

    @Test
    void disconnectRegistry_Success() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        UUID registryId = UUID.randomUUID();
        ContainerRegistry registry = new ContainerRegistry(projectId, orgId, "Docker Hub", "DOCKER_HUB", "https://index.docker.io/v1/", "TOKEN", "encryptedKey");
        registry.setId(registryId);

        when(registryRepository.findByIdAndProjectId(registryId, projectId)).thenReturn(Optional.of(registry));

        service.disconnectRegistry(projectId, registryId, userId);

        verify(registryRepository).delete(registry);
    }
}
