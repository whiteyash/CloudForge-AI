package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "repository_governance_policies")
public class RepositoryGovernancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false, unique = true)
    private UUID repositoryId;

    @Column(name = "branch_protection_enabled")
    private boolean branchProtectionEnabled = false;

    @Column(name = "required_reviews_count")
    private Integer requiredReviewsCount = 1;

    @Column(name = "signed_commits_required")
    private boolean signedCommitsRequired = false;

    @Column(name = "secret_scanning_enabled")
    private boolean secretScanningEnabled = false;

    @Column(name = "dependabot_enabled")
    private boolean dependabotEnabled = false;

    @Column(name = "code_scanning_enabled")
    private boolean codeScanningEnabled = false;

    @Column(name = "risk_score")
    private Integer riskScore = 20;

    @Column(name = "compliance_score")
    private Integer complianceScore = 80;

    @Column(name = "violation_count")
    private Integer violationCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RepositoryGovernancePolicy() {
    }

    public RepositoryGovernancePolicy(UUID repositoryId, boolean branchProtectionEnabled, Integer requiredReviewsCount, boolean signedCommitsRequired, boolean secretScanningEnabled, boolean dependabotEnabled, boolean codeScanningEnabled) {
        this.repositoryId = repositoryId;
        this.branchProtectionEnabled = branchProtectionEnabled;
        this.requiredReviewsCount = requiredReviewsCount != null ? requiredReviewsCount : 1;
        this.signedCommitsRequired = signedCommitsRequired;
        this.secretScanningEnabled = secretScanningEnabled;
        this.dependabotEnabled = dependabotEnabled;
        this.codeScanningEnabled = codeScanningEnabled;
        recalculateScores();
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
        recalculateScores();
    }

    public void recalculateScores() {
        int violations = 0;
        int compliance = 100;
        int risk = 0;

        if (!branchProtectionEnabled) {
            violations++;
            compliance -= 30;
            risk += 35;
        }
        if (requiredReviewsCount < 1) {
            violations++;
            compliance -= 20;
            risk += 20;
        }
        if (!signedCommitsRequired) {
            violations++;
            compliance -= 15;
            risk += 15;
        }
        if (!secretScanningEnabled) {
            violations++;
            compliance -= 20;
            risk += 20;
        }
        if (!dependabotEnabled) {
            violations++;
            compliance -= 15;
            risk += 10;
        }

        this.violationCount = violations;
        this.complianceScore = Math.max(compliance, 0);
        this.riskScore = Math.min(risk, 100);
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public boolean isBranchProtectionEnabled() {
        return branchProtectionEnabled;
    }

    public Integer getRequiredReviewsCount() {
        return requiredReviewsCount;
    }

    public boolean isSignedCommitsRequired() {
        return signedCommitsRequired;
    }

    public boolean isSecretScanningEnabled() {
        return secretScanningEnabled;
    }

    public boolean isDependabotEnabled() {
        return dependabotEnabled;
    }

    public boolean isCodeScanningEnabled() {
        return codeScanningEnabled;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public Integer getComplianceScore() {
        return complianceScore;
    }

    public Integer getViolationCount() {
        return violationCount;
    }

    public void updatePolicy(boolean branchProtectionEnabled, Integer requiredReviewsCount, boolean signedCommitsRequired, boolean secretScanningEnabled, boolean dependabotEnabled, boolean codeScanningEnabled) {
        this.branchProtectionEnabled = branchProtectionEnabled;
        this.requiredReviewsCount = requiredReviewsCount;
        this.signedCommitsRequired = signedCommitsRequired;
        this.secretScanningEnabled = secretScanningEnabled;
        this.dependabotEnabled = dependabotEnabled;
        this.codeScanningEnabled = codeScanningEnabled;
        recalculateScores();
    }
}
