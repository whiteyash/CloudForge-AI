package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.EventPublisher;

class RepositoryCommitSyncTest {

    private RepositoryCommitRepository commitRepository;
    private RepositorySyncService service;

    @BeforeEach
    void setUp() {
        ImportedRepositoryRepository repository = Mockito.mock(ImportedRepositoryRepository.class);
        RepositoryBranchRepository branchRepository = Mockito.mock(RepositoryBranchRepository.class);
        commitRepository = Mockito.mock(RepositoryCommitRepository.class);
        RepositoryContributorRepository contributorRepository = Mockito.mock(RepositoryContributorRepository.class);
        RepositorySyncJobRepository jobRepository = Mockito.mock(RepositorySyncJobRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        service = new RepositorySyncService(repository, branchRepository, commitRepository, contributorRepository, jobRepository, eventPublisher);
    }

    @Test
    void testGetCommitsForRepository() {
        UUID repoId = UUID.randomUUID();
        RepositoryCommit commit = new RepositoryCommit(
                repoId, "a1b2c3d4e5f678901234567890abcdef12345678", "Add initial pipeline config",
                "DevOps Admin", "devops@cloudforge.ai", Instant.now(), "https://github.com/org/repo/commit/a1b2c3d"
        );

        when(commitRepository.findByRepositoryIdOrderByCommittedAtDesc(repoId)).thenReturn(List.of(commit));

        List<RepositorySyncService.CommitResponse> commits = service.getCommitsForRepository(repoId);

        assertNotNull(commits);
        assertEquals(1, commits.size());
        assertEquals("a1b2c3d", commits.get(0).shortSha());
        assertEquals("Add initial pipeline config", commits.get(0).message());
    }
}
