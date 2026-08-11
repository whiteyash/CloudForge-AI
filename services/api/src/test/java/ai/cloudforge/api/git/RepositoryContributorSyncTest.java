package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.EventPublisher;

class RepositoryContributorSyncTest {

    private RepositoryContributorRepository contributorRepository;
    private RepositorySyncService service;

    @BeforeEach
    void setUp() {
        ImportedRepositoryRepository repository = Mockito.mock(ImportedRepositoryRepository.class);
        RepositoryBranchRepository branchRepository = Mockito.mock(RepositoryBranchRepository.class);
        RepositoryCommitRepository commitRepository = Mockito.mock(RepositoryCommitRepository.class);
        contributorRepository = Mockito.mock(RepositoryContributorRepository.class);
        RepositorySyncJobRepository jobRepository = Mockito.mock(RepositorySyncJobRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        service = new RepositorySyncService(repository, branchRepository, commitRepository, contributorRepository, jobRepository, eventPublisher);
    }

    @Test
    void testGetContributorsForRepository() {
        UUID repoId = UUID.randomUUID();
        RepositoryContributor contributor = new RepositoryContributor(
                repoId, "cloudforge-admin", "CloudForge Administrator", "https://github.com/cloudforge.png", 14
        );

        when(contributorRepository.findByRepositoryId(repoId)).thenReturn(List.of(contributor));

        List<RepositorySyncService.ContributorResponse> contributors = service.getContributorsForRepository(repoId);

        assertNotNull(contributors);
        assertEquals(1, contributors.size());
        assertEquals("cloudforge-admin", contributors.get(0).username());
        assertEquals(14, contributors.get(0).contributionCount());
    }
}
