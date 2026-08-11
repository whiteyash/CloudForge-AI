package ai.cloudforge.api.environment;

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
public class EnvironmentManagementController {

    private final EnvironmentManagementService service;

    public EnvironmentManagementController(EnvironmentManagementService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/environments")
    public ResponseEntity<List<EnvironmentManagementService.EnvironmentResponse>> listEnvironments(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getEnvironmentsForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/environments")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> createEnvironment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody CreateEnvironmentRequest request) {
        return ResponseEntity.ok(service.createEnvironment(
                orgId, principal.userId(), projectId, request.name(), request.environmentType(),
                request.description(), request.isProtected()
        ));
    }

    @GetMapping("/environments/{environmentId}")
    public ResponseEntity<EnvironmentManagementService.EnvironmentDetailResponse> getEnvironmentById(
            @PathVariable UUID environmentId) {
        return ResponseEntity.ok(service.getEnvironmentById(environmentId));
    }

    @PostMapping("/environments/{environmentId}/activate")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> activateEnvironment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID environmentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setStatus(orgId, principal.userId(), environmentId, "ACTIVE"));
    }

    @PostMapping("/environments/{environmentId}/deactivate")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> deactivateEnvironment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID environmentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setStatus(orgId, principal.userId(), environmentId, "INACTIVE"));
    }

    @PostMapping("/environments/{environmentId}/maintenance")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> setMaintenanceMode(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID environmentId,
            @RequestParam UUID orgId,
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(service.setMaintenanceMode(orgId, principal.userId(), environmentId, enabled));
    }

    @PostMapping("/environments/{environmentId}/freeze")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> freezeEnvironment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID environmentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setFrozenStatus(orgId, principal.userId(), environmentId, true));
    }

    @PostMapping("/environments/{environmentId}/unfreeze")
    public ResponseEntity<EnvironmentManagementService.EnvironmentResponse> unfreezeEnvironment(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID environmentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setFrozenStatus(orgId, principal.userId(), environmentId, false));
    }

    public record CreateEnvironmentRequest(
            String name,
            String environmentType,
            String description,
            boolean isProtected
    ) {}
}
