package ai.cloudforge.api.aiops;

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
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/incidents")
    public ResponseEntity<List<IncidentService.IncidentResponse>> listIncidents(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getIncidentsForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/incidents")
    public ResponseEntity<IncidentService.IncidentResponse> createIncident(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody CreateIncidentRequest request) {
        return ResponseEntity.ok(service.createIncident(
                orgId, principal.userId(), projectId, request.title(), request.severity(),
                request.rootCause(), request.confidenceScore()
        ));
    }

    @GetMapping("/incidents/{incidentId}")
    public ResponseEntity<IncidentService.IncidentDetailResponse> getIncidentById(
            @PathVariable UUID incidentId) {
        return ResponseEntity.ok(service.getIncidentById(incidentId));
    }

    @PostMapping("/incidents/{incidentId}/resolve")
    public ResponseEntity<IncidentService.IncidentResponse> resolveIncident(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID incidentId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.resolveIncident(orgId, principal.userId(), incidentId));
    }

    public record CreateIncidentRequest(
            String title,
            String severity,
            String rootCause,
            Double confidenceScore
    ) {}
}
