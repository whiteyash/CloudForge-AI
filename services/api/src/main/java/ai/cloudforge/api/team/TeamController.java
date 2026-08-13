package ai.cloudforge.api.team;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.team.TeamDtos.AddTeamMemberRequest;
import ai.cloudforge.api.team.TeamDtos.CreateTeamRequest;
import ai.cloudforge.api.team.TeamDtos.TeamMemberSummary;
import ai.cloudforge.api.team.TeamDtos.TeamResponse;
import ai.cloudforge.api.team.TeamDtos.UpdateTeamMemberRoleRequest;
import ai.cloudforge.api.team.TeamDtos.UpdateTeamRequest;

import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/orgs/{orgId}/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> listTeams(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @RequestParam(name = "search", required = false) String search) {
        return ResponseEntity.ok(teamService.getTeamsForOrg(principal.userId(), orgId, search, environment));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeam(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeam(principal.userId(), orgId, teamId));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(principal.userId(), orgId, request, environment));
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(principal.userId(), orgId, teamId, request, environment));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberSummary>> getMembers(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(principal.userId(), orgId, teamId));
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamResponse> addMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @RequestBody AddTeamMemberRequest request) {
        return ResponseEntity.ok(teamService.addMember(principal.userId(), orgId, teamId, request.userId(), request.role(), environment));
    }

    @PatchMapping("/{teamId}/members/{userId}")
    public ResponseEntity<TeamResponse> updateMemberRole(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @RequestBody UpdateTeamMemberRoleRequest request) {
        return ResponseEntity.ok(teamService.updateMemberRole(principal.userId(), orgId, teamId, userId, request.role(), environment));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<TeamResponse> removeMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment) {
        return ResponseEntity.ok(teamService.removeMember(principal.userId(), orgId, teamId, userId, environment));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment) {
        teamService.deleteTeam(principal.userId(), orgId, teamId, environment);
        return ResponseEntity.noContent().build();
    }
}
