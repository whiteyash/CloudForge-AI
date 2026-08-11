package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.List;
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

class RepositoryReleaseServiceTest {

    private RepositoryReleaseRepository repository;
    private EventPublisher eventPublisher;
    private RepositoryReleaseService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RepositoryReleaseRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositoryReleaseService(repository, eventPublisher);
    }

    @Test
    void testGetReleasesForRepository() {
        UUID repoId = UUID.randomUUID();
        RepositoryRelease release = new RepositoryRelease(
                repoId, "rel-v1.0.0", "v1.0.0", "v1.0.0 Production Release", "Release Notes",
                "cloudforge-bot", "https://github.com/avatar.png", false, false, Instant.now(), "https://github.com/releases/v1.0.0"
        );

        when(repository.findByRepositoryIdOrderByPublishedAtDesc(repoId)).thenReturn(List.of(release));

        List<RepositoryReleaseService.ReleaseResponse> releases = service.getReleasesForRepository(repoId);

        assertNotNull(releases);
        assertEquals(1, releases.size());
        assertEquals("v1.0.0", releases.get(0).tagName());
    }

    @Test
    void testSyncRelease() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();

        when(repository.findByRepositoryIdAndTagName(repoId, "v1.1.0")).thenReturn(Optional.empty());
        when(repository.save(any(RepositoryRelease.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositoryReleaseService.ReleaseResponse response = service.syncRelease(
                orgId, userId, repoId, "rel-v1.1.0", "v1.1.0", "v1.1.0 Feature Release", "Notes",
                "cloudforge-lead", null, false, false, Instant.now(), null
        );

        assertNotNull(response);
        assertEquals("v1.1.0", response.tagName());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
