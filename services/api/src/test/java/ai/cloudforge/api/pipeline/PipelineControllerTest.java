package ai.cloudforge.api.pipeline;

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

class PipelineControllerTest {

    private PipelineService service;
    private PipelineController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PipelineService.class);
        controller = new PipelineController(service);
    }

    @Test
    void testListPipelinesEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getPipelinesForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<PipelineService.PipelineResponse>> response = controller.listPipelines(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreatePipelineEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        PipelineController.CreatePipelineRequest request = new PipelineController.CreatePipelineRequest(
                null, "main-ci", "Main CI Pipeline", "name: main-ci"
        );

        when(service.createPipeline(orgId, userId, projectId, null, "main-ci", "Main CI Pipeline", "name: main-ci"))
                .thenReturn(new PipelineService.PipelineResponse(
                        UUID.randomUUID(), projectId, null, "main-ci", "Main CI Pipeline", "name: main-ci", "ACTIVE"
                ));

        ResponseEntity<PipelineService.PipelineResponse> response = controller.createPipeline(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
