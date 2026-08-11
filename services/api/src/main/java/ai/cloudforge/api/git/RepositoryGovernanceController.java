package ai.cloudforge.api.git;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class RepositoryGovernanceController {

    private final RepositoryGovernanceService service;

    public RepositoryGovernanceController(RepositoryGovernanceService service) {
        this.service = service;
    }

    @GetMapping("/repositories/{repositoryId}/governance")
    public ResponseEntity<RepositoryGovernanceService.GovernanceResponse> getGovernance(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getGovernancePolicy(repositoryId));
    }

    @PostMapping("/repositories/{repositoryId}/governance")
    public ResponseEntity<RepositoryGovernanceService.GovernanceResponse> updateGovernance(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID repositoryId,
            @RequestParam UUID orgId,
            @RequestBody GovernancePolicyRequest request) {
        return ResponseEntity.ok(service.updateGovernancePolicy(
                orgId,
                principal.userId(),
                repositoryId,
                request.branchProtectionEnabled(),
                request.requiredReviewsCount(),
                request.signedCommitsRequired(),
                request.secretScanningEnabled(),
                request.dependabotEnabled(),
                request.codeScanningEnabled()
        ));
    }

    public record GovernancePolicyRequest(
            boolean branchProtectionEnabled,
            Integer requiredReviewsCount,
            boolean signedCommitsRequired,
            boolean secretScanningEnabled,
            boolean dependabotEnabled,
            boolean codeScanningEnabled
    ) {}
}
