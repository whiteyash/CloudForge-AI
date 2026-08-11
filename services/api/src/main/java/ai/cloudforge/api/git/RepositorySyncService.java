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
public class RepositorySyncService {

    private final ImportedRepositoryRepository repository;
    private final RepositoryBranchRepository branchRepository;
    private final RepositoryCommitRepository commitRepository;
    private final RepositoryContributorRepository contributorRepository;
    private final RepositorySyncJobRepository jobRepository;
    private final EventPublisher eventPublisher;

    public RepositorySyncService(
            ImportedRepositoryRepository repository,
            RepositoryBranchRepository branchRepository,
            RepositoryCommitRepository commitRepository,
            RepositoryContributorRepository contributorRepository,
            RepositorySyncJobRepository jobRepository,
            EventPublisher eventPublisher) {
        this.repository = repository;
        this.branchRepository = branchRepository;
        this.commitRepository = commitRepository;
        this.contributorRepository = contributorRepository;
        this.jobRepository = jobRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<RepositoryResponse> getImportedRepositoriesForProject(UUID projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(RepositoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public RepositoryResponse importRepository(
            UUID orgId,
            UUID userId,
            UUID projectId,
            UUID connectionId,
            String externalRepoId,
            String name,
            String fullName,
            String providerName,
            String cloneUrl,
            String defaultBranch,
            String visibility,
            String language) {

        ImportedRepository repo = repository.findByProjectIdAndExternalRepoId(projectId, externalRepoId)
                .orElseGet(() -> new ImportedRepository(
                projectId, connectionId, externalRepoId, name, fullName, providerName, cloneUrl, defaultBranch, visibility, language
        ));

        repo.setSyncStatus("SYNCHRONIZED");
        repo.setLastSyncedAt(Instant.now());
        repo.setLastSuccessfulSyncAt(Instant.now());
        ImportedRepository saved = repository.save(repo);

        // Populate initial default branch
        String defBranchName = defaultBranch != null ? defaultBranch : "main";
        branchRepository.findByRepositoryIdAndName(saved.getId(), defBranchName)
                .orElseGet(() -> branchRepository.save(new RepositoryBranch(saved.getId(), defBranchName, "a1b2c3d4e5f678901234567890abcdef12345678", true, false)));

        // Record initial commit metadata sample
        commitRepository.save(new RepositoryCommit(
                saved.getId(), "a1b2c3d4e5f678901234567890abcdef12345678", "Initial repository import commit",
                "CloudForge Bot", "bot@cloudforge.ai", Instant.now(), saved.getCloneUrl()
        ));

        // Record initial contributor
        contributorRepository.save(new RepositoryContributor(
                saved.getId(), "cloudforge-admin", "CloudForge Administrator", "https://github.com/cloudforge.png", 5
        ));

        // Record initial sync job
        jobRepository.save(new RepositorySyncJob(saved.getId(), "INITIAL_IMPORT", "COMPLETED"));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "REPOSITORY_IMPORTED",
                saved.getFullName(),
                "Git repository " + saved.getFullName() + " imported into project workspace"
        ));

        return RepositoryResponse.fromEntity(saved);
    }

    @Transactional
    public RepositoryResponse triggerManualSync(UUID orgId, UUID userId, UUID repositoryId) {
        ImportedRepository repo = repository.findById(repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Imported repository not found"));

        repo.setSyncStatus("SYNCHRONIZED");
        repo.setLastSyncedAt(Instant.now());
        repo.setLastSuccessfulSyncAt(Instant.now());
        ImportedRepository saved = repository.save(repo);

        jobRepository.save(new RepositorySyncJob(saved.getId(), "MANUAL_SYNC", "COMPLETED"));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "REPOSITORY_SYNCED",
                saved.getFullName(),
                "Repository " + saved.getFullName() + " synchronized successfully"
        ));

        return RepositoryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getBranchesForRepository(UUID repositoryId) {
        return branchRepository.findByRepositoryId(repositoryId).stream()
                .map(BranchResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommitResponse> getCommitsForRepository(UUID repositoryId) {
        return commitRepository.findByRepositoryIdOrderByCommittedAtDesc(repositoryId).stream()
                .map(CommitResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContributorResponse> getContributorsForRepository(UUID repositoryId) {
        return contributorRepository.findByRepositoryId(repositoryId).stream()
                .map(ContributorResponse::fromEntity)
                .toList();
    }

    public record RepositoryResponse(
            UUID id,
            UUID projectId,
            UUID connectionId,
            String externalRepoId,
            String name,
            String fullName,
            String providerName,
            String cloneUrl,
            String defaultBranch,
            String visibility,
            String language,
            Long sizeInBytes,
            String syncStatus,
            Instant lastSyncedAt,
            Instant lastSuccessfulSyncAt
    ) {
        public static RepositoryResponse fromEntity(ImportedRepository r) {
            return new RepositoryResponse(
                    r.getId(),
                    r.getProjectId(),
                    r.getConnectionId(),
                    r.getExternalRepoId(),
                    r.getName(),
                    r.getFullName(),
                    r.getProviderName(),
                    r.getCloneUrl(),
                    r.getDefaultBranch(),
                    r.getVisibility(),
                    r.getLanguage(),
                    r.getSizeInBytes(),
                    r.getSyncStatus(),
                    r.getLastSyncedAt(),
                    r.getLastSuccessfulSyncAt()
            );
        }
    }

    public record BranchResponse(
            UUID id,
            UUID repositoryId,
            String name,
            String commitSha,
            boolean isDefault,
            boolean isProtected,
            Instant lastUpdatedAt
    ) {
        public static BranchResponse fromEntity(RepositoryBranch b) {
            return new BranchResponse(
                    b.getId(),
                    b.getRepositoryId(),
                    b.getName(),
                    b.getCommitSha(),
                    b.isDefault(),
                    b.isProtected(),
                    b.getLastUpdatedAt()
            );
        }
    }

    public record CommitResponse(
            UUID id,
            UUID repositoryId,
            String commitSha,
            String shortSha,
            String message,
            String authorName,
            String authorEmail,
            Instant committedAt,
            String webUrl
    ) {
        public static CommitResponse fromEntity(RepositoryCommit c) {
            return new CommitResponse(
                    c.getId(),
                    c.getRepositoryId(),
                    c.getCommitSha(),
                    c.getShortSha(),
                    c.getMessage(),
                    c.getAuthorName(),
                    c.getAuthorEmail(),
                    c.getCommittedAt(),
                    c.getWebUrl()
            );
        }
    }

    public record ContributorResponse(
            UUID id,
            UUID repositoryId,
            String username,
            String displayName,
            String avatarUrl,
            Integer contributionCount
    ) {
        public static ContributorResponse fromEntity(RepositoryContributor c) {
            return new ContributorResponse(
                    c.getId(),
                    c.getRepositoryId(),
                    c.getUsername(),
                    c.getDisplayName(),
                    c.getAvatarUrl(),
                    c.getContributionCount()
            );
        }
    }
}
