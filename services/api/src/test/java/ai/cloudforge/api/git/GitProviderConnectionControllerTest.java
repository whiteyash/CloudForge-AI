package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.cloudforge.api.auth.AuthPrincipal;

class GitProviderConnectionControllerTest {

    private GitProviderConnectionService service;
    private GitProviderConnectionController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(GitProviderConnectionService.class);
        controller = new GitProviderConnectionController(service);
    }

    @Test
    void testListConnectionsEndpoint() {
        UUID orgId = UUID.randomUUID();
        when(service.getConnectionsForOrg(orgId)).thenReturn(List.of());

        ResponseEntity<List<GitProviderConnectionService.ConnectionResponse>> response = controller.listConnections(orgId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testConnectProviderEndpoint() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        GitProviderConnectionController.ConnectRequest request = new GitProviderConnectionController.ConnectRequest(
                "GITHUB", "cloudforge-org", "access_token", "refresh_token", "repo"
        );

        when(service.connectProvider(userId, orgId, "GITHUB", "cloudforge-org", "access_token", "refresh_token", "repo"))
                .thenReturn(new GitProviderConnectionService.ConnectionResponse(
                        UUID.randomUUID(), orgId, "GITHUB", "cloudforge-org", "ACTIVE", "repo", "CONNECTED", 5000, null, null
                ));

        ResponseEntity<GitProviderConnectionService.ConnectionResponse> response = controller.connectProvider(principal, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GITHUB", response.getBody().providerName());
    }

    @Test
    void testDisconnectProviderEndpoint() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        ResponseEntity<Void> response = controller.disconnectProvider(principal, orgId, connectionId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).disconnectProvider(userId, orgId, connectionId);
    }
}
