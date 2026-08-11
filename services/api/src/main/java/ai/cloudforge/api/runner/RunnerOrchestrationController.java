package ai.cloudforge.api.runner;

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
public class RunnerOrchestrationController {

    private final RunnerOrchestrationService service;

    public RunnerOrchestrationController(RunnerOrchestrationService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/runners")
    public ResponseEntity<List<RunnerOrchestrationService.RunnerResponse>> listRunners(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getRunnersForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/runners/register")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> registerRunner(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody RegisterRunnerRequest request) {
        return ResponseEntity.ok(service.registerRunner(
                orgId, principal.userId(), projectId, request.name(), request.runnerType(),
                request.runnerGroup(), request.token(), request.labels(), request.operatingSystem(), request.maxParallelJobs()
        ));
    }

    @GetMapping("/runners/{runnerId}")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> getRunnerById(
            @PathVariable UUID runnerId) {
        return ResponseEntity.ok(service.getRunnerById(runnerId));
    }

    @PostMapping("/runners/{runnerId}/heartbeat")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> heartbeat(
            @PathVariable UUID runnerId) {
        return ResponseEntity.ok(service.heartbeat(runnerId));
    }

    @PostMapping("/runners/{runnerId}/enable")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> enableRunner(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID runnerId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setRunnerStatus(orgId, principal.userId(), runnerId, "ONLINE"));
    }

    @PostMapping("/runners/{runnerId}/disable")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> disableRunner(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID runnerId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setRunnerStatus(orgId, principal.userId(), runnerId, "DISABLED"));
    }

    @PostMapping("/runners/{runnerId}/drain")
    public ResponseEntity<RunnerOrchestrationService.RunnerResponse> drainRunner(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID runnerId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.setRunnerStatus(orgId, principal.userId(), runnerId, "DRAINING"));
    }

    public record RegisterRunnerRequest(
            String name,
            String runnerType,
            String runnerGroup,
            String token,
            String labels,
            String operatingSystem,
            Integer maxParallelJobs
    ) {}
}
