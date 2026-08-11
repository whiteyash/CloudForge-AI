package ai.cloudforge.api.observability;

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

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class ObservabilityController {

    private final ObservabilityService service;

    public ObservabilityController(ObservabilityService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/analytics/overview")
    public ResponseEntity<ObservabilityService.AnalyticsOverviewResponse> getOverview(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getOverviewForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/analytics/dora")
    public ResponseEntity<ObservabilityService.DoraMetricsResponse> getDoraMetrics(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getDoraMetricsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/alerts")
    public ResponseEntity<List<ObservabilityService.AlertResponse>> getAlerts(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getAlertsForProject(projectId));
    }

    @PostMapping("/alerts/{alertId}/acknowledge")
    public ResponseEntity<ObservabilityService.AlertResponse> acknowledgeAlert(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID alertId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.acknowledgeAlert(orgId, principal.userId(), alertId));
    }
}
