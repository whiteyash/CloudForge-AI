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

class RepositoryPullRequestSecurityTest {

    private RepositoryPullRequestRepository repository;
    private RepositoryPullRequestService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RepositoryPullRequestRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositoryPullRequestService(repository, eventPublisher);
    }

    @Test
    void testUnauthorizedPullRequestAccessThrowsNotFound() {
        UUID prId = UUID.randomUUID();

        when(repository.findById(prId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getPullRequestById(prId);
        });
    }
}
