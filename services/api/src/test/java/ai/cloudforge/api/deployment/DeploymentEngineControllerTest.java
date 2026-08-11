package ai.cloudforge.api.deployment;

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

class DeploymentEngineControllerTest {

    private DeploymentEngineService service;
    private DeploymentEngineController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(DeploymentEngineService.class);
        controller = new DeploymentEngineController(service);
    }

    @Test
    void testListDeploymentsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getDeploymentsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<DeploymentEngineService.DeploymentResponse>> response = controller.listDeployments(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateDeploymentEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        DeploymentEngineController.CreateDeploymentRequest request = new DeploymentEngineController.CreateDeploymentRequest(
                null, null, "STAGING", "ROLLING", "idem-1"
        );

        when(service.createDeployment(orgId, userId, projectId, null, null, "STAGING", "ROLLING", "idem-1", "test@cloudforge.ai"))
                .thenReturn(new DeploymentEngineService.DeploymentResponse(
                        UUID.randomUUID(), projectId, null, null, "STAGING", "ROLLING", "SUCCEEDED", "idem-1", "test@cloudforge.ai", null, null
                ));

        ResponseEntity<DeploymentEngineService.DeploymentResponse> response = controller.createDeployment(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("STAGING", response.getBody().targetName());
    }
}
