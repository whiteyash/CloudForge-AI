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
public class RepositoryReleaseService {

    private final RepositoryReleaseRepository repository;
    private final EventPublisher eventPublisher;

    public RepositoryReleaseService(RepositoryReleaseRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<ReleaseResponse> getReleasesForRepository(UUID repositoryId) {
        return repository.findByRepositoryIdOrderByPublishedAtDesc(repositoryId).stream()
                .map(ReleaseResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReleaseResponse getReleaseById(UUID releaseId) {
        return repository.findById(releaseId)
                .map(ReleaseResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Repository Release not found"));
    }

    @Transactional
    public ReleaseResponse syncRelease(
            UUID orgId,
            UUID userId,
            UUID repositoryId,
            String externalReleaseId,
            String tagName,
            String name,
            String body,
            String authorUsername,
            String authorAvatarUrl,
            boolean isDraft,
            boolean isPrerelease,
            Instant publishedAt,
            String webUrl) {

        RepositoryRelease release = repository.findByRepositoryIdAndTagName(repositoryId, tagName)
                .orElseGet(() -> new RepositoryRelease(
                repositoryId, externalReleaseId, tagName, name, body, authorUsername, authorAvatarUrl, isDraft, isPrerelease, publishedAt, webUrl
        ));

        RepositoryRelease saved = repository.save(release);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "RELEASE_SYNCED",
                tagName + " - " + name,
                "Release " + tagName + " (" + name + ") synchronized"
        ));

        return ReleaseResponse.fromEntity(saved);
    }

    public record ReleaseResponse(
            UUID id,
            UUID repositoryId,
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
    ) {
        public static ReleaseResponse fromEntity(RepositoryRelease r) {
            return new ReleaseResponse(
                    r.getId(),
                    r.getRepositoryId(),
                    r.getExternalReleaseId(),
                    r.getTagName(),
                    r.getName(),
                    r.getBody(),
                    r.getAuthorUsername(),
                    r.getAuthorAvatarUrl(),
                    r.isDraft(),
                    r.isPrerelease(),
                    r.getPublishedAt(),
                    r.getWebUrl()
            );
        }
    }
}
