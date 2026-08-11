package ai.cloudforge.api.pipeline;

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
public class PipelineController {

    private final PipelineService service;

    public PipelineController(PipelineService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/pipelines")
    public ResponseEntity<List<PipelineService.PipelineResponse>> listPipelines(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getPipelinesForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/pipelines")
    public ResponseEntity<PipelineService.PipelineResponse> createPipeline(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody CreatePipelineRequest request) {
        return ResponseEntity.ok(service.createPipeline(
                orgId, principal.userId(), projectId, request.repositoryId(),
                request.name(), request.description(), request.yamlDefinition()
        ));
    }

    @GetMapping("/pipelines/{pipelineId}")
    public ResponseEntity<PipelineService.PipelineResponse> getPipelineById(
            @PathVariable UUID pipelineId) {
        return ResponseEntity.ok(service.getPipelineById(pipelineId));
    }

    @PostMapping("/pipelines/{pipelineId}/trigger")
    public ResponseEntity<PipelineService.RunResponse> triggerRun(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID pipelineId,
            @RequestParam UUID orgId,
            @RequestParam(required = false, defaultValue = "manual") String triggeredBy) {
        return ResponseEntity.ok(service.triggerRun(orgId, principal.userId(), pipelineId, triggeredBy));
    }

    @GetMapping("/pipelines/{pipelineId}/runs")
    public ResponseEntity<List<PipelineService.RunResponse>> listRuns(
            @PathVariable UUID pipelineId) {
        return ResponseEntity.ok(service.getRunsForPipeline(pipelineId));
    }

    @GetMapping("/pipeline-runs/{runId}")
    public ResponseEntity<PipelineService.RunDetailResponse> getRunDetail(
            @PathVariable UUID runId) {
        return ResponseEntity.ok(service.getRunDetail(runId));
    }

    @PostMapping("/pipeline-runs/{runId}/cancel")
    public ResponseEntity<PipelineService.RunResponse> cancelRun(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID runId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.cancelRun(orgId, principal.userId(), runId));
    }

    @PostMapping("/pipeline-runs/{runId}/approve")
    public ResponseEntity<PipelineService.RunResponse> approveRun(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID runId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.approveRun(orgId, principal.userId(), runId));
    }

    public record CreatePipelineRequest(
            UUID repositoryId,
            String name,
            String description,
            String yamlDefinition
    ) {}
}
