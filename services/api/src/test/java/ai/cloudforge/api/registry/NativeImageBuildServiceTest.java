package ai.cloudforge.api.registry;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

class NativeImageBuildServiceTest {

    @Mock
    private NativeImageBuildRepository buildRepository;

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

    private NativeImageBuildService service;

    private UUID projectId;
    private UUID registryId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NativeImageBuildService(buildRepository, registryRepository, imageRepositoryRepository, imageTagRepository, projectRepository, rbacService);

        projectId = UUID.randomUUID();
        registryId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void triggerBuild_MissingDockerOrDockerfile_FailsHonestly() {
        Organization org = new Organization("CloudForge Org", "cloudforge-org");
        Project project = new Project(org, "CloudForge API", "https://github.com/cloudforge/api", "cloudforge-prod");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ContainerRegistry registry = new ContainerRegistry(projectId, UUID.randomUUID(), "GHCR", "GITHUB_GHCR", "https://ghcr.io", "TOKEN", "encrypted");
        registry.setId(registryId);
        when(registryRepository.findByIdAndProjectId(registryId, projectId)).thenReturn(Optional.of(registry));

        TriggerBuildRequest request = new TriggerBuildRequest();
        request.setRegistryId(registryId);
        request.setRepositoryName("myorg/payment-service");
        request.setTagName("latest");
        request.setDockerfilePath("NonExistentDockerfile");

        when(buildRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NativeImageBuildDto dto = service.triggerBuild(projectId, request, userId);

        assertNotNull(dto);
        assertEquals("myorg/payment-service", dto.getRepositoryName());
        assertEquals("latest", dto.getTagName());
        assertEquals("FAILED", dto.getStatus());
    }

    @Test
    void triggerBuild_ProjectNotFound_ThrowsResourceNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        TriggerBuildRequest request = new TriggerBuildRequest();
        request.setRegistryId(registryId);
        request.setRepositoryName("myorg/payment-service");
        request.setTagName("latest");

        assertThrows(ResourceNotFoundException.class, () -> service.triggerBuild(projectId, request, userId));
    }
}
