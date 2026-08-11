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
public class RepositorySyncController {

    private final RepositorySyncService service;

    public RepositorySyncController(RepositorySyncService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/imported-repositories")
    public ResponseEntity<List<RepositorySyncService.RepositoryResponse>> listImportedRepositories(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getImportedRepositoriesForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/imported-repositories/import")
    public ResponseEntity<RepositorySyncService.RepositoryResponse> importRepository(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody ImportRequest request) {
        return ResponseEntity.ok(service.importRepository(
                orgId,
                principal.userId(),
                projectId,
                request.connectionId(),
                request.externalRepoId(),
                request.name(),
                request.fullName(),
                request.providerName(),
                request.cloneUrl(),
                request.defaultBranch(),
                request.visibility(),
                request.language()
        ));
    }

    @PostMapping("/imported-repositories/{repositoryId}/sync")
    public ResponseEntity<RepositorySyncService.RepositoryResponse> triggerManualSync(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam UUID orgId,
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.triggerManualSync(orgId, principal.userId(), repositoryId));
    }

    @GetMapping("/imported-repositories/{repositoryId}/branches")
    public ResponseEntity<List<RepositorySyncService.BranchResponse>> listRepositoryBranches(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getBranchesForRepository(repositoryId));
    }

    @GetMapping("/imported-repositories/{repositoryId}/commits")
    public ResponseEntity<List<RepositorySyncService.CommitResponse>> listRepositoryCommits(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getCommitsForRepository(repositoryId));
    }

    @GetMapping("/imported-repositories/{repositoryId}/contributors")
    public ResponseEntity<List<RepositorySyncService.ContributorResponse>> listRepositoryContributors(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getContributorsForRepository(repositoryId));
    }

    public record ImportRequest(
            UUID connectionId,
            String externalRepoId,
            String name,
            String fullName,
            String providerName,
            String cloneUrl,
            String defaultBranch,
            String visibility,
            String language
    ) {}
}
