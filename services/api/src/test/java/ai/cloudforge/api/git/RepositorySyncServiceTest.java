package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class RepositorySyncServiceTest {

    private ImportedRepositoryRepository repository;
    private RepositoryBranchRepository branchRepository;
    private RepositoryCommitRepository commitRepository;
    private RepositoryContributorRepository contributorRepository;
    private RepositorySyncJobRepository jobRepository;
    private EventPublisher eventPublisher;
    private RepositorySyncService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ImportedRepositoryRepository.class);
        branchRepository = Mockito.mock(RepositoryBranchRepository.class);
        commitRepository = Mockito.mock(RepositoryCommitRepository.class);
        contributorRepository = Mockito.mock(RepositoryContributorRepository.class);
        jobRepository = Mockito.mock(RepositorySyncJobRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositorySyncService(repository, branchRepository, commitRepository, contributorRepository, jobRepository, eventPublisher);
    }

    @Test
    void testImportRepository() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();

        when(repository.findByProjectIdAndExternalRepoId(projectId, "12345"))
                .thenReturn(Optional.empty());
        when(repository.save(any(ImportedRepository.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RepositorySyncService.RepositoryResponse response = service.importRepository(
                orgId, userId, projectId, connectionId, "12345", "cloudforge-web", "cloudforge-ai/cloudforge-web",
                "GITHUB", "https://github.com/cloudforge-ai/cloudforge-web.git", "main", "PUBLIC", "TypeScript"
        );

        assertNotNull(response);
        assertEquals("cloudforge-web", response.name());
        assertEquals("SYNCHRONIZED", response.syncStatus());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testTriggerManualSync() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();

        ImportedRepository repo = new ImportedRepository(
                UUID.randomUUID(), UUID.randomUUID(), "12345", "cloudforge-web", "cloudforge-ai/cloudforge-web",
                "GITHUB", "https://github.com/cloudforge-ai/cloudforge-web.git", "main", "PUBLIC", "TypeScript"
        );

        when(repository.findById(repoId)).thenReturn(Optional.of(repo));
        when(repository.save(any(ImportedRepository.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositorySyncService.RepositoryResponse response = service.triggerManualSync(orgId, userId, repoId);

        assertNotNull(response);
        assertEquals("SYNCHRONIZED", response.syncStatus());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
