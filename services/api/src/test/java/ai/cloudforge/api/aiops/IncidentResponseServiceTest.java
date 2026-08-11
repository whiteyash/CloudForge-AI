package ai.cloudforge.api.aiops;

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

import ai.cloudforge.api.aiops.IncidentDtos.IncidentResponseDto;
import ai.cloudforge.api.aiops.IncidentDtos.TriggerIncidentRequest;
import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class IncidentResponseServiceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private OnCallScheduleRepository onCallScheduleRepository;
    @Mock private AlertIntegrationChannelRepository alertChannelRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private RbacService rbacService;

    private IncidentResponseService service;
    private UUID projectId;
    private UUID orgId;
    private UUID userId;
    private Project project;

    @BeforeEach
    void setUp() {
        service = new IncidentResponseService(
                incidentRepository,
                onCallScheduleRepository,
                alertChannelRepository,
                projectRepository,
                rbacService,
                "TestIncidentSecretEncryption32BytesKey!"
        );

        projectId = UUID.randomUUID();
        Organization org = new Organization("CloudForge AIOps", "cloudforge-aiops");
        orgId = org.getId();

        project = new Project(org, "AIOps Project", "https://github.com/cloudforge/aiops", "aiops-proj");
        project.setId(projectId);
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should trigger incident successfully")
    void triggerIncident_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "INCIDENT_MANAGE");
        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> {
            Incident inc = i.getArgument(0);
            if (inc.getId() == null) inc.setId(UUID.randomUUID());
            return inc;
        });

        TriggerIncidentRequest request = new TriggerIncidentRequest("High Latency Warning", "Latency spike on gateway", "SEV_2_HIGH", "PROMETHEUS");
        IncidentResponseDto response = service.triggerIncident(projectId, request, userId);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("High Latency Warning");
        assertThat(response.severity()).isEqualTo("SEV_2_HIGH");
        assertThat(response.status()).isEqualTo("TRIGGERED");

        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is unauthenticated")
    void triggerIncident_Unauthenticated() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        TriggerIncidentRequest request = new TriggerIncidentRequest("High Latency Warning", "Desc", "SEV_2_HIGH", "PROMETHEUS");
        assertThatThrownBy(() -> service.triggerIncident(projectId, request, null))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Unauthenticated request");
    }

    @Test
    @DisplayName("Should acknowledge incident")
    void acknowledgeIncident_Success() {
        UUID incidentId = UUID.randomUUID();
        Incident incident = new Incident(projectId, orgId, "DB Connection Timeout", "Connection pool exhausted", "SEV_1_CRITICAL", "PROMETHEUS");
        incident.setId(incidentId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "INCIDENT_MANAGE");
        when(incidentRepository.findByProjectIdAndId(projectId, incidentId)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> i.getArgument(0));

        IncidentResponseDto response = service.acknowledgeIncident(projectId, incidentId, null, userId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("ACKNOWLEDGED");
        assertThat(response.acknowledgedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should resolve incident")
    void resolveIncident_Success() {
        UUID incidentId = UUID.randomUUID();
        Incident incident = new Incident(projectId, orgId, "DB Connection Timeout", "Connection pool exhausted", "SEV_1_CRITICAL", "PROMETHEUS");
        incident.setId(incidentId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(rbacService).requirePermission(userId, orgId, "INCIDENT_MANAGE");
        when(incidentRepository.findByProjectIdAndId(projectId, incidentId)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(i -> i.getArgument(0));

        IncidentResponseDto response = service.resolveIncident(projectId, incidentId, userId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("RESOLVED");
        assertThat(response.resolvedAt()).isNotNull();
    }
}
