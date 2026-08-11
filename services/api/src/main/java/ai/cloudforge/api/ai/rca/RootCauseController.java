package ai.cloudforge.api.ai.rca;

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
public class RootCauseController {

    private final RootCauseAnalysisService service;

    public RootCauseController(RootCauseAnalysisService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/root-causes")
    public ResponseEntity<List<RootCauseAnalysisService.RootCauseReportResponse>> listRootCauses(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getReportsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/root-causes/graphs")
    public ResponseEntity<List<DependencyGraphService.DependencyLinkResponse>> getGraphs(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getGraphForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/root-causes/analyze")
    public ResponseEntity<AIResponse<RootCauseAnalysisService.RootCauseReportResponse>> analyzeRootCause(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID incidentId) {
        return ResponseEntity.ok(service.performRootCauseAnalysis(orgId, principal.userId(), projectId, incidentId));
    }
}
