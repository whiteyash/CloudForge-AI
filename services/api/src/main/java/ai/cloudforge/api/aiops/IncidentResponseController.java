package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.aiops.IncidentDtos.AcknowledgeIncidentRequest;
import ai.cloudforge.api.aiops.IncidentDtos.AlertChannelDto;
import ai.cloudforge.api.aiops.IncidentDtos.CreateAlertChannelRequest;
import ai.cloudforge.api.aiops.IncidentDtos.CreateOnCallScheduleRequest;
import ai.cloudforge.api.aiops.IncidentDtos.IncidentResponseDto;
import ai.cloudforge.api.aiops.IncidentDtos.IncidentSummaryDto;
import ai.cloudforge.api.aiops.IncidentDtos.OnCallScheduleDto;
import ai.cloudforge.api.aiops.IncidentDtos.TriggerIncidentRequest;
import ai.cloudforge.api.auth.AuthPrincipal;
import jakarta.validation.Valid;

@RestController
public class IncidentResponseController {

    private final IncidentResponseService incidentService;

    public IncidentResponseController(IncidentResponseService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/api/incidents/summary")
    public ResponseEntity<IncidentSummaryDto> getGlobalSummary(@AuthenticationPrincipal AuthPrincipal principal) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.getIncidentSummary(null, userId));
    }

    @GetMapping("/projects/{projectId}/incidents/summary")
    public ResponseEntity<IncidentSummaryDto> getProjectSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.getIncidentSummary(projectId, userId));
    }

    @PostMapping("/projects/{projectId}/incidents/trigger")
    public ResponseEntity<IncidentResponseDto> triggerIncident(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @Valid @RequestBody TriggerIncidentRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        IncidentResponseDto created = incidentService.triggerIncident(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/projects/{projectId}/incidents/dispatcher")
    public ResponseEntity<List<IncidentResponseDto>> listIncidents(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.listIncidents(projectId, userId));
    }

    @GetMapping("/projects/{projectId}/incidents/dispatcher/{incidentId}")
    public ResponseEntity<IncidentResponseDto> getIncidentDetails(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID incidentId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.getIncidentDetails(projectId, incidentId, userId));
    }

    @PostMapping("/projects/{projectId}/incidents/{incidentId}/acknowledge")
    public ResponseEntity<IncidentResponseDto> acknowledgeIncident(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID incidentId,
            @RequestBody(required = false) AcknowledgeIncidentRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.acknowledgeIncident(projectId, incidentId, request, userId));
    }

    @PostMapping("/projects/{projectId}/incidents/{incidentId}/resolve")
    public ResponseEntity<IncidentResponseDto> resolveIncident(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID incidentId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.resolveIncident(projectId, incidentId, userId));
    }

    @PostMapping("/organizations/{orgId}/oncall-schedules")
    public ResponseEntity<OnCallScheduleDto> createOnCallSchedule(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @Valid @RequestBody CreateOnCallScheduleRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        OnCallScheduleDto created = incidentService.createOnCallSchedule(orgId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/organizations/{orgId}/oncall-schedules")
    public ResponseEntity<List<OnCallScheduleDto>> listOnCallSchedules(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.listOnCallSchedules(orgId, userId));
    }

    @PostMapping("/organizations/{orgId}/alert-channels")
    public ResponseEntity<AlertChannelDto> createAlertChannel(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @Valid @RequestBody CreateAlertChannelRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        AlertChannelDto created = incidentService.createAlertChannel(orgId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/organizations/{orgId}/alert-channels")
    public ResponseEntity<List<AlertChannelDto>> listAlertChannels(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(incidentService.listAlertChannels(orgId, userId));
    }
}
