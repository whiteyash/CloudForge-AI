package ai.cloudforge.api.auth;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.SecurityCenterService.SecurityOverviewResponse;

@RestController
@RequestMapping("/profile")
public class SecurityCenterController {

    private final SecurityCenterService securityCenterService;

    public SecurityCenterController(SecurityCenterService securityCenterService) {
        this.securityCenterService = securityCenterService;
    }

    @GetMapping("/security-overview")
    public ResponseEntity<SecurityOverviewResponse> getSecurityOverview(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(securityCenterService.getSecurityOverview(principal.userId()));
    }

    @PostMapping("/favorite-workspaces/{orgId}")
    public ResponseEntity<Void> addFavoriteWorkspace(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        securityCenterService.addFavoriteWorkspace(principal.userId(), orgId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorite-workspaces/{orgId}")
    public ResponseEntity<Void> removeFavoriteWorkspace(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        securityCenterService.removeFavoriteWorkspace(principal.userId(), orgId);
        return ResponseEntity.noContent().build();
    }
}
