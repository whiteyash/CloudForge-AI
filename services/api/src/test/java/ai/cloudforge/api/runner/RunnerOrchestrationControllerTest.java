package ai.cloudforge.api.runner;

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

class RunnerOrchestrationControllerTest {

    private RunnerOrchestrationService service;
    private RunnerOrchestrationController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(RunnerOrchestrationService.class);
        controller = new RunnerOrchestrationController(service);
    }

    @Test
    void testListRunnersEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getRunnersForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<RunnerOrchestrationService.RunnerResponse>> response = controller.listRunners(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testRegisterRunnerEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        RunnerOrchestrationController.RegisterRunnerRequest request = new RunnerOrchestrationController.RegisterRunnerRequest(
                "runner-agent-1", "DOCKER", "default", "tok123", "ubuntu-latest", "linux", 2
        );

        when(service.registerRunner(orgId, userId, projectId, "runner-agent-1", "DOCKER", "default", "tok123", "ubuntu-latest", "linux", 2))
                .thenReturn(new RunnerOrchestrationService.RunnerResponse(
                        UUID.randomUUID(), projectId, "runner-agent-1", "DOCKER", "default", "ONLINE", "ubuntu-latest", "linux", 2, 0
                ));

        ResponseEntity<RunnerOrchestrationService.RunnerResponse> response = controller.registerRunner(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("runner-agent-1", response.getBody().name());
    }
}
