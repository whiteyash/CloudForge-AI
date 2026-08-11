package ai.cloudforge.api.ai.prediction;

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
public class PredictiveOperationsController {

    private final PredictiveOperationsService service;

    public PredictiveOperationsController(PredictiveOperationsService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/predictions")
    public ResponseEntity<List<PredictiveOperationsService.PredictiveForecastResponse>> listPredictions(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getForecastsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/predictions/capacity")
    public ResponseEntity<List<CapacityForecastService.CapacityForecastResult>> getCapacity(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getCapacityForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/predictions/analyze")
    public ResponseEntity<AIResponse<PredictiveOperationsService.PredictiveForecastResponse>> analyzePredictions(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.generatePredictiveReport(orgId, principal.userId(), projectId));
    }
}
