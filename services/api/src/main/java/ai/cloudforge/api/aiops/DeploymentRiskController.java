package ai.cloudforge.api.aiops;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class DeploymentRiskController {

    private final DeploymentRiskEngine riskEngine;

    public DeploymentRiskController(DeploymentRiskEngine riskEngine) {
        this.riskEngine = riskEngine;
    }

    @GetMapping("/projects/{projectId}/deployment-risk/{deploymentId}")
    public ResponseEntity<DeploymentRiskEngine.RiskAssessmentResponse> getDeploymentRisk(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID deploymentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(riskEngine.evaluateDeploymentRisk(orgId, principal.userId(), projectId, deploymentId));
    }
}
