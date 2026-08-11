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

class RepositorySyncControllerTest {

    private RepositorySyncService service;
    private RepositorySyncController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(RepositorySyncService.class);
        controller = new RepositorySyncController(service);
    }

    @Test
    void testListImportedRepositoriesEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getImportedRepositoriesForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<RepositorySyncService.RepositoryResponse>> response = controller.listImportedRepositories(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testImportRepositoryEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        RepositorySyncController.ImportRequest request = new RepositorySyncController.ImportRequest(
                connectionId, "12345", "cloudforge-web", "cloudforge-ai/cloudforge-web",
                "GITHUB", "https://github.com/cloudforge-ai/cloudforge-web.git", "main", "PUBLIC", "TypeScript"
        );

        when(service.importRepository(orgId, userId, projectId, connectionId, "12345", "cloudforge-web", "cloudforge-ai/cloudforge-web", "GITHUB", "https://github.com/cloudforge-ai/cloudforge-web.git", "main", "PUBLIC", "TypeScript"))
                .thenReturn(new RepositorySyncService.RepositoryResponse(
                        UUID.randomUUID(), projectId, connectionId, "12345", "cloudforge-web", "cloudforge-ai/cloudforge-web", "GITHUB", "https://github.com/cloudforge-ai/cloudforge-web.git", "main", "PUBLIC", "TypeScript", 0L, "SYNCHRONIZED", null, null
                ));

        ResponseEntity<RepositorySyncService.RepositoryResponse> response = controller.importRepository(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("cloudforge-web", response.getBody().name());
    }

    @Test
    void testListRepositoryBranchesEndpoint() {
        UUID repositoryId = UUID.randomUUID();
        when(service.getBranchesForRepository(repositoryId)).thenReturn(List.of());

        ResponseEntity<List<RepositorySyncService.BranchResponse>> response = controller.listRepositoryBranches(repositoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testListRepositoryCommitsEndpoint() {
        UUID repositoryId = UUID.randomUUID();
        when(service.getCommitsForRepository(repositoryId)).thenReturn(List.of());

        ResponseEntity<List<RepositorySyncService.CommitResponse>> response = controller.listRepositoryCommits(repositoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testListRepositoryContributorsEndpoint() {
        UUID repositoryId = UUID.randomUUID();
        when(service.getContributorsForRepository(repositoryId)).thenReturn(List.of());

        ResponseEntity<List<RepositorySyncService.ContributorResponse>> response = controller.listRepositoryContributors(repositoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
