package ai.cloudforge.api.git;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping("/orgs/{orgId}/git-connections")
public class GitProviderConnectionController {

    private final GitProviderConnectionService service;

    public GitProviderConnectionController(GitProviderConnectionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<GitProviderConnectionService.ConnectionResponse>> listConnections(
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(service.getConnectionsForOrg(orgId));
    }

    @GetMapping("/oauth/authorize")
    public ResponseEntity<Map<String, String>> generateAuthUrl(
            @PathVariable UUID orgId,
            @RequestParam String providerName) {
        return ResponseEntity.ok(Map.of("authorizeUrl", service.generateAuthUrl(orgId, providerName)));
    }

    @PostMapping
    public ResponseEntity<GitProviderConnectionService.ConnectionResponse> connectProvider(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @RequestBody ConnectRequest request) {
        return ResponseEntity.ok(service.connectProvider(
                principal.userId(),
                orgId,
                request.providerName(),
                request.accountName(),
                request.accessToken(),
                request.refreshToken(),
                request.scopes()
        ));
    }

    @PostMapping("/{connectionId}/refresh")
    public ResponseEntity<GitProviderConnectionService.ConnectionResponse> refreshConnection(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID connectionId) {
        return ResponseEntity.ok(service.refreshConnection(principal.userId(), orgId, connectionId));
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> disconnectProvider(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID connectionId) {
        service.disconnectProvider(principal.userId(), orgId, connectionId);
        return ResponseEntity.noContent().build();
    }

    public record ConnectRequest(
            String providerName,
            String accountName,
            String accessToken,
            String refreshToken,
            String scopes
    ) {}
}
