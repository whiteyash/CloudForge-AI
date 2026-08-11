package ai.cloudforge.api.ai.operations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

class ApprovalWorkflowServiceTest {

    private RemediationPlanRepository planRepository;
    private ApprovalRequestRepository requestRepository;
    private ApprovalActionRepository actionRepository;
    private ExecutionDelegationService executionDelegationService;
    private SafetyValidationService safetyValidationService;
    private ApprovalWorkflowService service;

    @BeforeEach
    void setUp() {
        planRepository = Mockito.mock(RemediationPlanRepository.class);
        requestRepository = Mockito.mock(ApprovalRequestRepository.class);
        actionRepository = Mockito.mock(ApprovalActionRepository.class);
        executionDelegationService = Mockito.mock(ExecutionDelegationService.class);
        safetyValidationService = Mockito.mock(SafetyValidationService.class);

        service = new ApprovalWorkflowService(
                planRepository, requestRepository, actionRepository,
                executionDelegationService, safetyValidationService
        );
    }

    @Test
    void testApproveAndExecuteWorkflow() {
        UUID planId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        RemediationPlan plan = new RemediationPlan(
                projectId, approverId, "Title", "DEPLOYMENT", "Summary",
                96, "Evidence", "Risk", "Rollback", "Impact", "Perms", "PENDING_APPROVAL"
        );

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(safetyValidationService.validateRemediationSafety(any(), any())).thenReturn(
                new SafetyValidationService.SafetyCheckResult(true, "OK", "OK", "OK", "OK")
        );
        when(requestRepository.save(any(ApprovalRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(executionDelegationService.delegateExecution(any(), any())).thenReturn(
                new ExecutionHistory(planId, approverId, "DeploymentEngineService", "SUCCESS", "Logs")
        );

        ExecutionHistory history = service.approveAndExecute(planId, approverId, "Looks good");

        assertNotNull(history);
        assertEquals("SUCCESS", history.getStatus());
    }
}
