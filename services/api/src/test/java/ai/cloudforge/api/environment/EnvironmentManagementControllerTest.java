package ai.cloudforge.api.environment;

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

class EnvironmentManagementControllerTest {

    private EnvironmentManagementService service;
    private EnvironmentManagementController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(EnvironmentManagementService.class);
        controller = new EnvironmentManagementController(service);
    }

    @Test
    void testListEnvironmentsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getEnvironmentsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<EnvironmentManagementService.EnvironmentResponse>> response = controller.listEnvironments(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateEnvironmentEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        EnvironmentManagementController.CreateEnvironmentRequest request = new EnvironmentManagementController.CreateEnvironmentRequest(
                "Staging", "STAGING", "Staging env", false
        );

        when(service.createEnvironment(orgId, userId, projectId, "Staging", "STAGING", "Staging env", false))
                .thenReturn(new EnvironmentManagementService.EnvironmentResponse(
                        UUID.randomUUID(), projectId, "Staging", "STAGING", "Staging env", "ACTIVE", false, false, false, "HEALTHY"
                ));

        ResponseEntity<EnvironmentManagementService.EnvironmentResponse> response = controller.createEnvironment(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Staging", response.getBody().name());
    }
}
