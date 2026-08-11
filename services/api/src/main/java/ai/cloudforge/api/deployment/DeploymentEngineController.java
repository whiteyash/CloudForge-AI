package ai.cloudforge.api.deployment;

import java.util.List;
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
public class DeploymentEngineController {

    private final DeploymentEngineService service;

    public DeploymentEngineController(DeploymentEngineService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/deployments")
    public ResponseEntity<List<DeploymentEngineService.DeploymentResponse>> listDeployments(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getDeploymentsForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/deployments")
    public ResponseEntity<DeploymentEngineService.DeploymentResponse> createDeployment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody CreateDeploymentRequest request) {
        return ResponseEntity.ok(service.createDeployment(
                orgId, principal.userId(), projectId, request.pipelineRunId(), request.artifactId(),
                request.targetName(), request.strategy(), request.idempotencyKey(), principal.email()
        ));
    }

    @GetMapping("/deployments/{deploymentId}")
    public ResponseEntity<DeploymentEngineService.DeploymentResponse> getDeploymentById(
            @PathVariable UUID deploymentId) {
        return ResponseEntity.ok(service.getDeploymentById(deploymentId));
    }

    @PostMapping("/deployments/{deploymentId}/approve")
    public ResponseEntity<DeploymentEngineService.DeploymentResponse> approveDeployment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID deploymentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.approveDeployment(orgId, principal.userId(), deploymentId, principal.email()));
    }

    @PostMapping("/deployments/{deploymentId}/cancel")
    public ResponseEntity<DeploymentEngineService.DeploymentResponse> cancelDeployment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID deploymentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.cancelDeployment(orgId, principal.userId(), deploymentId));
    }

    @PostMapping("/deployments/{deploymentId}/rollback")
    public ResponseEntity<DeploymentEngineService.DeploymentResponse> rollbackDeployment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID deploymentId,
            @RequestParam UUID orgId,
            @RequestBody RollbackRequest request) {
        return ResponseEntity.ok(service.rollbackDeployment(
                orgId, principal.userId(), deploymentId, request.targetDeploymentId(), request.reason(), principal.email()
        ));
    }

    public record CreateDeploymentRequest(
            UUID pipelineRunId,
            UUID artifactId,
            String targetName,
            String strategy,
            String idempotencyKey
    ) {}

    public record RollbackRequest(
            UUID targetDeploymentId,
            String reason
    ) {}
}
