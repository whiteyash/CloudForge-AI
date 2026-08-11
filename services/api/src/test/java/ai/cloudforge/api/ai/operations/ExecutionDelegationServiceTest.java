package ai.cloudforge.api.ai.operations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.deployment.DeploymentEngineService;

class ExecutionDelegationServiceTest {

    private ExecutionHistoryRepository executionHistoryRepository;
    private ExecutionDelegationService service;

    @BeforeEach
    void setUp() {
        executionHistoryRepository = Mockito.mock(ExecutionHistoryRepository.class);
        DeploymentEngineService deploymentEngineService = Mockito.mock(DeploymentEngineService.class);

        service = new ExecutionDelegationService(executionHistoryRepository, deploymentEngineService);
    }

    @Test
    void testDelegateExecutionToDeploymentService() {
        UUID planId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RemediationPlan plan = new RemediationPlan(
                UUID.randomUUID(), userId, "Title", "DEPLOYMENT", "Summary",
                96, "Evidence", "Risk", "Rollback", "Impact", "Perms", "APPROVED"
        );

        when(executionHistoryRepository.save(any(ExecutionHistory.class))).thenAnswer(inv -> inv.getArgument(0));

        ExecutionHistory history = service.delegateExecution(plan, userId);

        assertNotNull(history);
        assertEquals("DeploymentEngineService", history.getExecutionService());
        assertEquals("EXECUTED", plan.getStatus());
    }
}
