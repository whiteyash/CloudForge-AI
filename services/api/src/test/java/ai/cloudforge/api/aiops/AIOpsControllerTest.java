package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.cloudforge.api.auth.AuthPrincipal;

class AIOpsControllerTest {

    private IncidentService incidentService;
    private IncidentController incidentController;

    @BeforeEach
    void setUp() {
        incidentService = Mockito.mock(IncidentService.class);
        incidentController = new IncidentController(incidentService);
    }

    @Test
    void testListIncidentsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(incidentService.getIncidentsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<IncidentService.IncidentResponse>> response = incidentController.listIncidents(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateIncidentEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        IncidentController.CreateIncidentRequest request = new IncidentController.CreateIncidentRequest(
                "Deployment Timeout", "HIGH", "Memory leak in pod", 0.90
        );

        when(incidentService.createIncident(orgId, userId, projectId, "Deployment Timeout", "HIGH", "Memory leak in pod", 0.90))
                .thenReturn(new IncidentService.IncidentResponse(
                        UUID.randomUUID(), projectId, "Deployment Timeout", "HIGH", "OPEN", "Memory leak in pod", 0.90
                ));

        ResponseEntity<IncidentService.IncidentResponse> response = incidentController.createIncident(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Deployment Timeout", response.getBody().title());
    }
}
