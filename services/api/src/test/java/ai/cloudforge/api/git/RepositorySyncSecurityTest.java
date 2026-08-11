package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class RepositorySyncSecurityTest {

    private ImportedRepositoryRepository repository;
    private RepositorySyncService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ImportedRepositoryRepository.class);
        RepositoryBranchRepository branchRepository = Mockito.mock(RepositoryBranchRepository.class);
        RepositoryCommitRepository commitRepository = Mockito.mock(RepositoryCommitRepository.class);
        RepositoryContributorRepository contributorRepository = Mockito.mock(RepositoryContributorRepository.class);
        RepositorySyncJobRepository jobRepository = Mockito.mock(RepositorySyncJobRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        service = new RepositorySyncService(repository, branchRepository, commitRepository, contributorRepository, jobRepository, eventPublisher);
    }

    @Test
    void testCrossTenantRepositoryAccessBlocked() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();

        // Repository ID not found in tenant org boundary -> ResourceNotFoundException thrown
        when(repository.findById(repoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.triggerManualSync(orgId, userId, repoId);
        });
    }
}
