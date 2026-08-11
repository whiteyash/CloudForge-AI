package ai.cloudforge.api.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.OrganizationActivityService.ActivityTimelineResponse;
import ai.cloudforge.api.auth.OrganizationActivityService.OrgDashboardSummaryResponse;

@RestController
@RequestMapping
public class OrganizationActivityController {

    private final OrganizationActivityService activityService;

    public OrganizationActivityController(OrganizationActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/orgs/{orgId}/activity-timeline")
    public ResponseEntity<List<ActivityTimelineResponse>> getActivityTimeline(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(activityService.getActivityTimeline(principal.userId(), orgId));
    }

    @GetMapping("/orgs/{orgId}/dashboard-summary")
    public ResponseEntity<OrgDashboardSummaryResponse> getDashboardSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(activityService.getDashboardSummary(principal.userId(), orgId));
    }

    @PostMapping("/auth/switch-workspace")
    public ResponseEntity<Void> switchWorkspace(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody SwitchWorkspaceRequest request) {
        activityService.switchWorkspace(principal.userId(), request.targetOrgId());
        return ResponseEntity.noContent().build();
    }

    public record SwitchWorkspaceRequest(
            UUID targetOrgId
    ) {}
}
