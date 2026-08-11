package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class RepositoryPullRequestService {

    private final RepositoryPullRequestRepository repository;
    private final EventPublisher eventPublisher;

    public RepositoryPullRequestService(RepositoryPullRequestRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<PullRequestResponse> getPullRequestsForRepository(UUID repositoryId) {
        return repository.findByRepositoryIdOrderByCreatedAtDesc(repositoryId).stream()
                .map(PullRequestResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PullRequestResponse getPullRequestById(UUID prId) {
        return repository.findById(prId)
                .map(PullRequestResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Pull Request not found"));
    }

    @Transactional
    public PullRequestResponse createOrUpdatePullRequest(
            UUID orgId,
            UUID userId,
            UUID repositoryId,
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
            String webUrl) {

        RepositoryPullRequest pr = repository.findByRepositoryIdAndNumber(repositoryId, number)
                .orElseGet(() -> new RepositoryPullRequest(
                repositoryId, externalPrId, number, title, description, state, authorUsername, authorAvatarUrl, sourceBranch, targetBranch, isDraft, webUrl
        ));

        RepositoryPullRequest saved = repository.save(pr);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "PULL_REQUEST_SYNCED",
                "#" + number + " " + title,
                "Pull Request #" + number + " (" + state + ") synchronized"
        ));

        return PullRequestResponse.fromEntity(saved);
    }

    public record PullRequestResponse(
            UUID id,
            UUID repositoryId,
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
            String webUrl,
            Instant createdAt
    ) {
        public static PullRequestResponse fromEntity(RepositoryPullRequest pr) {
            return new PullRequestResponse(
                    pr.getId(),
                    pr.getRepositoryId(),
                    pr.getExternalPrId(),
                    pr.getNumber(),
                    pr.getTitle(),
                    pr.getDescription(),
                    pr.getState(),
                    pr.getAuthorUsername(),
                    pr.getAuthorAvatarUrl(),
                    pr.getSourceBranch(),
                    pr.getTargetBranch(),
                    pr.isDraft(),
                    pr.getWebUrl(),
                    pr.getCreatedAt()
            );
        }
    }
}
