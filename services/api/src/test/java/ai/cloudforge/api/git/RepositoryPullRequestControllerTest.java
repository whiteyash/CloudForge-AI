package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.cloudforge.api.auth.AuthPrincipal;

class RepositoryPullRequestControllerTest {

    private RepositoryPullRequestService service;
    private RepositoryPullRequestController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(RepositoryPullRequestService.class);
        controller = new RepositoryPullRequestController(service);
    }

    @Test
    void testListPullRequestsEndpoint() {
        UUID repositoryId = UUID.randomUUID();
        when(service.getPullRequestsForRepository(repositoryId)).thenReturn(List.of());

        ResponseEntity<List<RepositoryPullRequestService.PullRequestResponse>> response = controller.listPullRequests(repositoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPullRequestByIdEndpoint() {
        UUID prId = UUID.randomUUID();
        when(service.getPullRequestById(prId)).thenReturn(new RepositoryPullRequestService.PullRequestResponse(
                prId, UUID.randomUUID(), "pr-101", 101, "Fix pipeline", "Desc", "OPEN", "author", null, "feat", "main", false, null, null
        ));

        ResponseEntity<RepositoryPullRequestService.PullRequestResponse> response = controller.getPullRequestById(prId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(101, response.getBody().number());
    }

    @Test
    void testCreateOrUpdatePullRequestEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repositoryId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        RepositoryPullRequestController.CreatePrRequest request = new RepositoryPullRequestController.CreatePrRequest(
                "pr-102", 102, "Add OAuth Integration", "OAuth PR", "MERGED", "devops-lead", null, "feature/oauth", "main", false, null
        );

        when(service.createOrUpdatePullRequest(orgId, userId, repositoryId, "pr-102", 102, "Add OAuth Integration", "OAuth PR", "MERGED", "devops-lead", null, "feature/oauth", "main", false, null))
                .thenReturn(new RepositoryPullRequestService.PullRequestResponse(
                        UUID.randomUUID(), repositoryId, "pr-102", 102, "Add OAuth Integration", "OAuth PR", "MERGED", "devops-lead", null, "feature/oauth", "main", false, null, null
                ));

        ResponseEntity<RepositoryPullRequestService.PullRequestResponse> response = controller.createOrUpdatePullRequest(principal, repositoryId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("MERGED", response.getBody().state());
    }
}
