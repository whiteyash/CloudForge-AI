package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

class RepositoryInsightsServiceTest {

    private RepositoryBranchRepository branchRepository;
    private RepositoryCommitRepository commitRepository;
    private RepositoryContributorRepository contributorRepository;
    private RepositoryPullRequestRepository prRepository;
    private RepositoryInsightsSnapshotRepository snapshotRepository;
    private RepositoryInsightsService service;

    @BeforeEach
    void setUp() {
        branchRepository = Mockito.mock(RepositoryBranchRepository.class);
        commitRepository = Mockito.mock(RepositoryCommitRepository.class);
        contributorRepository = Mockito.mock(RepositoryContributorRepository.class);
        prRepository = Mockito.mock(RepositoryPullRequestRepository.class);
        snapshotRepository = Mockito.mock(RepositoryInsightsSnapshotRepository.class);

        service = new RepositoryInsightsService(
                branchRepository, commitRepository, contributorRepository, prRepository, snapshotRepository
        );
    }

    @Test
    void testGetInsightsForRepository() {
        UUID repoId = UUID.randomUUID();

        when(branchRepository.findByRepositoryId(repoId)).thenReturn(List.of());
        when(commitRepository.findByRepositoryIdOrderByCommittedAtDesc(repoId)).thenReturn(List.of());
        when(contributorRepository.findByRepositoryId(repoId)).thenReturn(List.of());
        when(prRepository.findByRepositoryIdOrderByCreatedAtDesc(repoId)).thenReturn(List.of());

        RepositoryInsightsService.InsightsSummary summary = service.getInsightsForRepository(repoId);

        assertNotNull(summary);
        assertEquals(0, summary.totalBranches());
        assertEquals(0, summary.totalCommits());
        assertEquals(45, summary.healthScore()); // 100 - 30 (no contributors) - 25 (no commits)
    }

    @Test
    void testCalculateHealthScore() {
        assertEquals(100, service.calculateHealthScore(2, 50, 5, 2));
        assertEquals(70, service.calculateHealthScore(2, 50, 0, 2));
    }
}
