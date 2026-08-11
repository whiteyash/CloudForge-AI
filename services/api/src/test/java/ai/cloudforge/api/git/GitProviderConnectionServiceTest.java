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

import ai.cloudforge.api.notification.EventPublisher;

class GitProviderConnectionServiceTest {

    private GitProviderConnectionRepository repository;
    private GitProviderConnectionService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(GitProviderConnectionRepository.class);
        GitEncryptionService encryptionService = new GitEncryptionService();
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new GitProviderConnectionService(repository, encryptionService, eventPublisher);
    }

    @Test
    void testConnectProviderLifecycle() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        String provider = "GITHUB";
        String account = "cloudforge-org";
        String token = "ghp_token123";

        when(repository.findByOrgIdAndProviderNameAndAccountName(orgId, provider, account))
                .thenReturn(Optional.empty());
        when(repository.save(any(GitProviderConnection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GitProviderConnectionService.ConnectionResponse response = service.connectProvider(userId, orgId, provider, account, token, null, "repo,read:org");

        assertNotNull(response);
        assertEquals(provider, response.providerName());
        assertEquals(account, response.accountName());
        assertEquals("CONNECTED", response.healthStatus());
    }

    @Test
    void testDisconnectProviderLifecycle() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID connId = UUID.randomUUID();

        GitProviderConnection conn = new GitProviderConnection(orgId, "GITHUB", "cloudforge-org", "enc_token", null, "repo");

        when(repository.findById(connId)).thenReturn(Optional.of(conn));

        service.disconnectProvider(userId, orgId, connId);

        verify(repository).delete(conn);
    }
}
