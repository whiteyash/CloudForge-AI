package ai.cloudforge.api.git;

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

class RepositoryPullRequestServiceTest {

    private RepositoryPullRequestRepository repository;
    private EventPublisher eventPublisher;
    private RepositoryPullRequestService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RepositoryPullRequestRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositoryPullRequestService(repository, eventPublisher);
    }

    @Test
    void testGetPullRequestsForRepository() {
        UUID repoId = UUID.randomUUID();
        RepositoryPullRequest pr = new RepositoryPullRequest(
                repoId, "pr-101", 101, "Fix CI pipeline failure", "Resolved runner timeout", "OPEN",
                "cloudforge-dev", "https://github.com/avatar.png", "fix/ci-timeout", "main", false, "https://github.com/org/repo/pull/101"
        );

        when(repository.findByRepositoryIdOrderByCreatedAtDesc(repoId)).thenReturn(List.of(pr));

        List<RepositoryPullRequestService.PullRequestResponse> prs = service.getPullRequestsForRepository(repoId);

        assertNotNull(prs);
        assertEquals(1, prs.size());
        assertEquals(101, prs.get(0).number());
        assertEquals("OPEN", prs.get(0).state());
    }

    @Test
    void testCreateOrUpdatePullRequest() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();

        when(repository.findByRepositoryIdAndNumber(repoId, 102)).thenReturn(Optional.empty());
        when(repository.save(any(RepositoryPullRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositoryPullRequestService.PullRequestResponse response = service.createOrUpdatePullRequest(
                orgId, userId, repoId, "pr-102", 102, "Add OAuth 2.0 Provider Connection", "OAuth implementation",
                "MERGED", "cloudforge-lead", "https://github.com/avatar2.png", "feature/oauth", "main", false, "https://github.com/org/repo/pull/102"
        );

        assertNotNull(response);
        assertEquals(102, response.number());
        assertEquals("MERGED", response.state());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
