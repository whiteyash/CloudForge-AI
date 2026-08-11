package ai.cloudforge.api.ai.log;

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
public class LogIntelligenceController {

    private final LogIntelligenceService service;
    private final LogSummaryService summaryService;

    public LogIntelligenceController(LogIntelligenceService service, LogSummaryService summaryService) {
        this.service = service;
        this.summaryService = summaryService;
    }

    @GetMapping("/projects/{projectId}/logs")
    public ResponseEntity<List<LogIntelligenceService.LogEntryResponse>> listLogs(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getLogsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/logs/clusters")
    public ResponseEntity<List<LogIntelligenceService.ClusterResponse>> listClusters(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getClustersForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/logs/fingerprints")
    public ResponseEntity<List<LogIntelligenceService.FingerprintResponse>> listFingerprints(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getFingerprintsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/logs/summary")
    public ResponseEntity<LogSummaryService.LogSummaryReport> getLogSummary(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(summaryService.generateLogSummary(List.of()));
    }

    @PostMapping("/projects/{projectId}/logs/analyze")
    public ResponseEntity<AIResponse<LogIntelligenceService.LogAnalysisResultResponse>> analyzeLog(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestParam UUID logEntryId) {
        return ResponseEntity.ok(service.analyzeLog(orgId, principal.userId(), projectId, logEntryId));
    }
}
