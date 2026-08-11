package ai.cloudforge.api.git;

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
public class RepositoryPullRequestController {

    private final RepositoryPullRequestService service;

    public RepositoryPullRequestController(RepositoryPullRequestService service) {
        this.service = service;
    }

    @GetMapping("/repositories/{repositoryId}/pull-requests")
    public ResponseEntity<List<RepositoryPullRequestService.PullRequestResponse>> listPullRequests(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getPullRequestsForRepository(repositoryId));
    }

    @GetMapping("/pull-requests/{prId}")
    public ResponseEntity<RepositoryPullRequestService.PullRequestResponse> getPullRequestById(
            @PathVariable UUID prId) {
        return ResponseEntity.ok(service.getPullRequestById(prId));
    }

    @PostMapping("/repositories/{repositoryId}/pull-requests")
    public ResponseEntity<RepositoryPullRequestService.PullRequestResponse> createOrUpdatePullRequest(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID repositoryId,
            @RequestParam UUID orgId,
            @RequestBody CreatePrRequest request) {
        return ResponseEntity.ok(service.createOrUpdatePullRequest(
                orgId,
                principal.userId(),
                repositoryId,
                request.externalPrId(),
                request.number(),
                request.title(),
                request.description(),
                request.state(),
                request.authorUsername(),
                request.authorAvatarUrl(),
                request.sourceBranch(),
                request.targetBranch(),
                request.isDraft(),
                request.webUrl()
        ));
    }

    public record CreatePrRequest(
            String externalPrId,
            Integer number,
            String title,
            String description,
            String state,
            String authorUsername,
            String authorAvatarUrl,
            String sourceBranch,
            String targetBranch,
            boolean isDraft,
            String webUrl
    ) {}
}
