package ai.cloudforge.api.ai.operations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.EvidenceCollector;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.MockLLMProvider;
import ai.cloudforge.api.ai.core.RecommendationFormatter;

class OperationsSecurityTest {

    private RemediationPlanRepository planRepository;
    private ApprovalWorkflowService approvalWorkflowService;
    private RemediationPlanningService service;

    @BeforeEach
    void setUp() {
        planRepository = Mockito.mock(RemediationPlanRepository.class);
        approvalWorkflowService = Mockito.mock(ApprovalWorkflowService.class);
        LLMProvider llmProvider = new MockLLMProvider();
        EvidenceCollector evidenceCollector = new EvidenceCollector();
        RecommendationFormatter recommendationFormatter = new RecommendationFormatter();
        AuditLogger auditLogger = new AuditLogger();

        service = new RemediationPlanningService(
                planRepository, approvalWorkflowService, llmProvider,
                evidenceCollector, recommendationFormatter, auditLogger
        );
    }

    @Test
    void testTenantScopedRemediationPlanning() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(planRepository.save(any(RemediationPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        AIResponse<RemediationPlanningService.RemediationPlanResponse> response = service.generateRemediationPlan(
                orgId, userId, projectId, "RUNNER", "Runner memory pressure"
        );
        assertNotNull(response);
    }
}
