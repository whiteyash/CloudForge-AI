package ai.cloudforge.api.git;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class RepositoryGovernanceService {

    private final RepositoryGovernancePolicyRepository repository;
    private final EventPublisher eventPublisher;

    public RepositoryGovernanceService(RepositoryGovernancePolicyRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public GovernanceResponse getGovernancePolicy(UUID repositoryId) {
        RepositoryGovernancePolicy policy = repository.findByRepositoryId(repositoryId)
                .orElseGet(() -> new RepositoryGovernancePolicy(repositoryId, true, 2, true, true, true, true));
        return GovernanceResponse.fromEntity(policy);
    }

    @Transactional
    public GovernanceResponse updateGovernancePolicy(
            UUID orgId,
            UUID userId,
            UUID repositoryId,
            boolean branchProtectionEnabled,
            Integer requiredReviewsCount,
            boolean signedCommitsRequired,
            boolean secretScanningEnabled,
            boolean dependabotEnabled,
            boolean codeScanningEnabled) {

        RepositoryGovernancePolicy policy = repository.findByRepositoryId(repositoryId)
                .orElseGet(() -> new RepositoryGovernancePolicy(
                repositoryId, branchProtectionEnabled, requiredReviewsCount, signedCommitsRequired, secretScanningEnabled, dependabotEnabled, codeScanningEnabled
        ));

        policy.updatePolicy(branchProtectionEnabled, requiredReviewsCount, signedCommitsRequired, secretScanningEnabled, dependabotEnabled, codeScanningEnabled);
        RepositoryGovernancePolicy saved = repository.save(policy);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "GOVERNANCE_AUDITED",
                "Compliance: " + saved.getComplianceScore() + "%",
                "Repository governance policy evaluated. Compliance score: " + saved.getComplianceScore() + "%"
        ));

        return GovernanceResponse.fromEntity(saved);
    }

    public record GovernanceResponse(
            UUID id,
            UUID repositoryId,
            boolean branchProtectionEnabled,
            Integer requiredReviewsCount,
            boolean signedCommitsRequired,
            boolean secretScanningEnabled,
            boolean dependabotEnabled,
            boolean codeScanningEnabled,
            Integer riskScore,
            Integer complianceScore,
            Integer violationCount
    ) {
        public static GovernanceResponse fromEntity(RepositoryGovernancePolicy p) {
            return new GovernanceResponse(
                    p.getId(),
                    p.getRepositoryId(),
                    p.isBranchProtectionEnabled(),
                    p.getRequiredReviewsCount(),
                    p.isSignedCommitsRequired(),
                    p.isSecretScanningEnabled(),
                    p.isDependabotEnabled(),
                    p.isCodeScanningEnabled(),
                    p.getRiskScore(),
                    p.getComplianceScore(),
                    p.getViolationCount()
            );
        }
    }
}
