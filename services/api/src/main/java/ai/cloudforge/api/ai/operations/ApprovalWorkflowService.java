package ai.cloudforge.api.ai.operations;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalWorkflowService {

    private final RemediationPlanRepository planRepository;
    private final ApprovalRequestRepository requestRepository;
    private final ApprovalActionRepository actionRepository;
    private final ExecutionDelegationService executionDelegationService;
    private final SafetyValidationService safetyValidationService;

    public ApprovalWorkflowService(
            RemediationPlanRepository planRepository,
            ApprovalRequestRepository requestRepository,
            ApprovalActionRepository actionRepository,
            ExecutionDelegationService executionDelegationService,
            SafetyValidationService safetyValidationService) {
        this.planRepository = planRepository;
        this.requestRepository = requestRepository;
        this.actionRepository = actionRepository;
        this.executionDelegationService = executionDelegationService;
        this.safetyValidationService = safetyValidationService;
    }

    @Transactional
    public ApprovalRequest submitForApproval(RemediationPlan plan, UUID userId) {
        plan.setStatus("PENDING_APPROVAL");
        planRepository.save(plan);
        return requestRepository.save(new ApprovalRequest(plan.getId(), userId, "PENDING"));
    }

    @Transactional
    public ExecutionHistory approveAndExecute(UUID planId, UUID approverId, String comments) {
        RemediationPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation plan not found: " + planId));

        SafetyValidationService.SafetyCheckResult safety = safetyValidationService.validateRemediationSafety(plan.getProjectId(), plan.getTargetType());
        if (!safety.safeToProceed()) {
            throw new IllegalStateException("Safety validation failed. Cannot approve plan.");
        }

        plan.setStatus("APPROVED");
        planRepository.save(plan);

        ApprovalRequest request = requestRepository.findByPlanId(planId)
                .orElseGet(() -> requestRepository.save(new ApprovalRequest(planId, approverId, "PENDING")));

        request.setStatus("APPROVED");
        requestRepository.save(request);

        actionRepository.save(new ApprovalAction(request.getId(), approverId, "APPROVE", comments));

        return executionDelegationService.delegateExecution(plan, approverId);
    }

    @Transactional
    public RemediationPlan rejectPlan(UUID planId, UUID approverId, String comments) {
        RemediationPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Remediation plan not found: " + planId));

        plan.setStatus("REJECTED");
        planRepository.save(plan);

        ApprovalRequest request = requestRepository.findByPlanId(planId)
                .orElseGet(() -> requestRepository.save(new ApprovalRequest(planId, approverId, "PENDING")));

        request.setStatus("REJECTED");
        requestRepository.save(request);

        actionRepository.save(new ApprovalAction(request.getId(), approverId, "REJECT", comments));

        return plan;
    }
}
