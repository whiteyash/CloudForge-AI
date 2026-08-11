package ai.cloudforge.api.ai.operations;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class OperationsController {

    private final RemediationPlanningService planningService;
    private final ApprovalWorkflowService approvalWorkflowService;
    private final ExecutionHistoryRepository executionHistoryRepository;

    public OperationsController(
            RemediationPlanningService planningService,
            ApprovalWorkflowService approvalWorkflowService,
            ExecutionHistoryRepository executionHistoryRepository) {
        this.planningService = planningService;
        this.approvalWorkflowService = approvalWorkflowService;
        this.executionHistoryRepository = executionHistoryRepository;
    }

    @PostMapping("/projects/{projectId}/operations/remediation")
    public ResponseEntity<AIResponse<RemediationPlanningService.RemediationPlanResponse>> createRemediationPlan(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestParam String targetType,
            @RequestParam String issueDescription) {
        return ResponseEntity.ok(planningService.generateRemediationPlan(orgId, principal.userId(), projectId, targetType, issueDescription));
    }

    @GetMapping("/projects/{projectId}/operations/plans")
    public ResponseEntity<List<RemediationPlanningService.RemediationPlanResponse>> getPlans(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(planningService.getPlansForProject(projectId));
    }

    @GetMapping("/operations/plans/{planId}")
    public ResponseEntity<RemediationPlanningService.RemediationPlanResponse> getPlan(
            @PathVariable UUID planId) {
        return ResponseEntity.ok(planningService.getPlanById(planId));
    }

    @PostMapping("/operations/plans/{planId}/approve")
    public ResponseEntity<ExecutionHistory> approvePlan(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID planId,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(approvalWorkflowService.approveAndExecute(planId, principal.userId(), comments));
    }

    @PostMapping("/operations/plans/{planId}/reject")
    public ResponseEntity<RemediationPlan> rejectPlan(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID planId,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(approvalWorkflowService.rejectPlan(planId, principal.userId(), comments));
    }

    @GetMapping("/operations/executions/{executionId}")
    public ResponseEntity<ExecutionHistory> getExecution(
            @PathVariable UUID executionId) {
        return ResponseEntity.of(executionHistoryRepository.findById(executionId));
    }
}
