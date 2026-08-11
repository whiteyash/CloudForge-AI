package ai.cloudforge.api.team;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.team.TeamDtos.AddTeamMemberRequest;
import ai.cloudforge.api.team.TeamDtos.CreateTeamRequest;
import ai.cloudforge.api.team.TeamDtos.TeamResponse;

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
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(teamService.getTeamsForOrg(principal.userId(), orgId));
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
            @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(principal.userId(), orgId, request));
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamResponse> addMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @RequestBody AddTeamMemberRequest request) {
        return ResponseEntity.ok(teamService.addMember(principal.userId(), orgId, teamId, request.userId()));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<TeamResponse> removeMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(teamService.removeMember(principal.userId(), orgId, teamId, userId));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID teamId) {
        teamService.deleteTeam(principal.userId(), orgId, teamId);
        return ResponseEntity.noContent().build();
    }
}
