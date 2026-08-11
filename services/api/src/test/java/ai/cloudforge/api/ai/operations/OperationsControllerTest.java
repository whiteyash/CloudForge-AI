package ai.cloudforge.api.ai.operations;

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

class OperationsControllerTest {

    private RemediationPlanningService planningService;
    private ApprovalWorkflowService approvalWorkflowService;
    private ExecutionHistoryRepository executionHistoryRepository;
    private OperationsController controller;

    @BeforeEach
    void setUp() {
        planningService = Mockito.mock(RemediationPlanningService.class);
        approvalWorkflowService = Mockito.mock(ApprovalWorkflowService.class);
        executionHistoryRepository = Mockito.mock(ExecutionHistoryRepository.class);
        controller = new OperationsController(planningService, approvalWorkflowService, executionHistoryRepository);
    }

    @Test
    void testGetPlansEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(planningService.getPlansForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<RemediationPlanningService.RemediationPlanResponse>> response = controller.getPlans(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
