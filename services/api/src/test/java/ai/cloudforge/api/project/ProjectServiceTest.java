package ai.cloudforge.api.project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;
import ai.cloudforge.api.auth.RbacService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private RbacService rbacService;
    @Mock
    private AuditLogRepository auditLogRepository;

    private ProjectService projectService;

    private final UUID userId = UUID.randomUUID();
    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, organizationRepository, rbacService, auditLogRepository);
    }

    @Test
    void getProjectsForOrg_returnsProjectList() {
        Organization org = new Organization("Acme Corp", "acme-corp");
        Project project = new Project(org, "Payments Service", "https://github.com/acme/payments", "payments-ns");

        when(projectRepository.findByOrganizationId(orgId)).thenReturn(List.of(project));

        List<ProjectDtos.ProjectResponse> projects = projectService.getProjectsForOrg(userId, orgId);

        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).name()).isEqualTo("Payments Service");
        assertThat(projects.get(0).k8sNamespace()).isEqualTo("payments-ns");
    }

    @Test
    void createProject_asEngineer_createsProjectAndWritesAuditLog() {
        Organization org = new Organization("Acme Corp", "acme-corp");
        ProjectDtos.CreateProjectRequest request = new ProjectDtos.CreateProjectRequest("Auth Service", "https://github.com/acme/auth", "auth-ns");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(projectRepository.existsByOrganizationIdAndNameIgnoreCase(orgId, "Auth Service")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDtos.ProjectResponse response = projectService.createProject(userId, orgId, request);

        assertThat(response.name()).isEqualTo("Auth Service");
        assertThat(response.k8sNamespace()).isEqualTo("auth-ns");

        verify(rbacService).requireMutatingPermission(userId, orgId);
        verify(auditLogRepository).save(any());
    }

    @Test
    void createProject_asViewer_throwsForbiddenException() {
        ProjectDtos.CreateProjectRequest request = new ProjectDtos.CreateProjectRequest("Auth Service", null, null);

        doThrow(new ForbiddenException("Access denied: Viewer role is read-only"))
                .when(rbacService).requireMutatingPermission(userId, orgId);

        assertThatThrownBy(() -> projectService.createProject(userId, orgId, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Viewer role is read-only");
    }
}
