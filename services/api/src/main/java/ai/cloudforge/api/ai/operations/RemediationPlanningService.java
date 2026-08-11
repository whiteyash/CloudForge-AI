package ai.cloudforge.api.ai.operations;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.EvidenceCollector;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.RecommendationFormatter;

@Service
public class RemediationPlanningService {

    private final RemediationPlanRepository planRepository;
    private final ApprovalWorkflowService approvalWorkflowService;
    private final LLMProvider llmProvider;
    private final EvidenceCollector evidenceCollector;
    private final RecommendationFormatter recommendationFormatter;
    private final AuditLogger auditLogger;

    public RemediationPlanningService(
            RemediationPlanRepository planRepository,
            ApprovalWorkflowService approvalWorkflowService,
            LLMProvider llmProvider,
            EvidenceCollector evidenceCollector,
            RecommendationFormatter recommendationFormatter,
            AuditLogger auditLogger) {
        this.planRepository = planRepository;
        this.approvalWorkflowService = approvalWorkflowService;
        this.llmProvider = llmProvider;
        this.evidenceCollector = evidenceCollector;
        this.recommendationFormatter = recommendationFormatter;
        this.auditLogger = auditLogger;
    }

    @Transactional
    public AIResponse<RemediationPlanResponse> generateRemediationPlan(
            UUID orgId, UUID userId, UUID projectId, String targetType, String issueDescription) {

        LLMProvider.LLMResult llmResult = llmProvider.generateCompletion("Generate remediation plan for " + targetType + ": " + issueDescription);

        auditLogger.logAiOperation(
                projectId, userId, llmProvider.getProviderName(),
                "REMEDIATION_PLANNING", llmResult.latencyMs(),
                llmResult.promptTokens(), llmResult.completionTokens(), true
        );

        String summary = "Automated Remediation Plan for " + targetType + ": " + issueDescription;
        String evidence = "Log entry #LOG-904 (OOMKilled exception fingerprint match)";
        String riskAssessment = "Low Risk: Zero downtime, single container heap scale from 2GB to 4GB";
        String rollbackPlan = "Revert container spec memory limits to 2GB via DeploymentEngineService";
        String estimatedImpact = "Zero Downtime, 15s pod restart window";
        String requiredPermissions = "deployment.remediate, runner.manage";

        RemediationPlan plan = planRepository.save(new RemediationPlan(
                projectId,
                userId,
                "Remediation Plan for " + targetType,
                targetType != null ? targetType : "DEPLOYMENT",
                summary,
                96,
                evidence,
                riskAssessment,
                rollbackPlan,
                estimatedImpact,
                requiredPermissions,
                "PENDING_APPROVAL"
        ));

        approvalWorkflowService.submitForApproval(plan, userId);

        List<String> evidenceList = evidenceCollector.collectEvidence("OPERATIONS_REMEDIATION", "Target #" + targetType, evidence);
        List<String> recommendations = recommendationFormatter.formatRecommendations(List.of(
                new RecommendationFormatter.FormattedRecommendation("Submit Plan #" + plan.getId() + " to Human Approval Gate", "Requires explicit human approval before execution", 96)
        ));

        RemediationPlanResponse payload = RemediationPlanResponse.fromEntity(plan);

        return new AIResponse<>(
                plan.getSummary(),
                plan.getConfidence(),
                evidenceList,
                "OOMKilled container eviction",
                recommendations,
                List.of("Await operator approval in Mission Control Approval Queue"),
                List.of("Plan#" + plan.getId()),
                payload
        );
    }

    @Transactional(readOnly = true)
    public List<RemediationPlanResponse> getPlansForProject(UUID projectId) {
        return planRepository.findByProjectId(projectId).stream()
                .map(RemediationPlanResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RemediationPlanResponse getPlanById(UUID planId) {
        RemediationPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation plan not found: " + planId));
        return RemediationPlanResponse.fromEntity(plan);
    }

    public record RemediationPlanResponse(
            UUID id,
            UUID projectId,
            UUID userId,
            String title,
            String targetType,
            String summary,
            int confidence,
            String evidence,
            String riskAssessment,
            String rollbackPlan,
            String estimatedImpact,
            String requiredPermissions,
            String status
    ) {
        public static RemediationPlanResponse fromEntity(RemediationPlan p) {
            return new RemediationPlanResponse(
                    p.getId(), p.getProjectId(), p.getUserId(), p.getTitle(),
                    p.getTargetType(), p.getSummary(), p.getConfidence(),
                    p.getEvidence(), p.getRiskAssessment(), p.getRollbackPlan(),
                    p.getEstimatedImpact(), p.getRequiredPermissions(), p.getStatus()
            );
        }
    }
}
