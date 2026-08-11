package ai.cloudforge.api.git;

import java.time.Instant;
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
public class RepositoryReleaseController {

    private final RepositoryReleaseService service;

    public RepositoryReleaseController(RepositoryReleaseService service) {
        this.service = service;
    }

    @GetMapping("/repositories/{repositoryId}/releases")
    public ResponseEntity<List<RepositoryReleaseService.ReleaseResponse>> listReleases(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getReleasesForRepository(repositoryId));
    }

    @GetMapping("/releases/{releaseId}")
    public ResponseEntity<RepositoryReleaseService.ReleaseResponse> getReleaseById(
            @PathVariable UUID releaseId) {
        return ResponseEntity.ok(service.getReleaseById(releaseId));
    }

    @PostMapping("/repositories/{repositoryId}/releases")
    public ResponseEntity<RepositoryReleaseService.ReleaseResponse> syncRelease(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID repositoryId,
            @RequestParam UUID orgId,
            @RequestBody SyncReleaseRequest request) {
        return ResponseEntity.ok(service.syncRelease(
                orgId,
                principal.userId(),
                repositoryId,
                request.externalReleaseId(),
                request.tagName(),
                request.name(),
                request.body(),
                request.authorUsername(),
                request.authorAvatarUrl(),
                request.isDraft(),
                request.isPrerelease(),
                request.publishedAt(),
                request.webUrl()
        ));
    }

    public record SyncReleaseRequest(
            String externalReleaseId,
            String tagName,
            String name,
            String body,
            String authorUsername,
            String authorAvatarUrl,
            boolean isDraft,
            boolean isPrerelease,
            Instant publishedAt,
            String webUrl
    ) {}
}
