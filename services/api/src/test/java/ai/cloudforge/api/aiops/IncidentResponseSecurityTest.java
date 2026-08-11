package ai.cloudforge.api.aiops;

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

import ai.cloudforge.api.aiops.IncidentDtos.TriggerIncidentRequest;
import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

class IncidentResponseSecurityTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private OnCallScheduleRepository onCallScheduleRepository;
    @Mock private AlertIntegrationChannelRepository alertChannelRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private RbacService rbacService;

    private IncidentResponseService service;
    private UUID projectId;
    private UUID orgId;
    private UUID unauthorizedUserId;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new IncidentResponseService(
                incidentRepository,
                onCallScheduleRepository,
                alertChannelRepository,
                projectRepository,
                rbacService,
                "TestSecurityIncidentSecretKey32B!"
        );

        unauthorizedUserId = UUID.randomUUID();

        Organization org = new Organization("Org A AIOps", "org-a-aiops");
        orgId = org.getId();

        project = new Project(org, "Project AIOps A", "https://github.com/org-a/aiops", "proj-aiops-a");
        projectId = project.getId();
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user lacks INCIDENT_MANAGE permission")
    void triggerIncident_Forbidden() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doThrow(new ForbiddenException("User lacks permission INCIDENT_MANAGE"))
                .when(rbacService).requirePermission(unauthorizedUserId, orgId, "INCIDENT_MANAGE");

        TriggerIncidentRequest request = new TriggerIncidentRequest("Unauthorized Incident", "Desc", "SEV_1_CRITICAL", "SYSTEM");

        assertThrows(ForbiddenException.class, () -> service.triggerIncident(projectId, request, unauthorizedUserId));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is unauthenticated (null userId)")
    void listIncidents_Unauthenticated_Forbidden() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(ForbiddenException.class, () -> service.listIncidents(projectId, null));
    }
}
