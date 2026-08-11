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

class TokenRefreshServiceTest {

    private GitProviderConnectionRepository repository;
    private EventPublisher eventPublisher;
    private GitProviderConnectionService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(GitProviderConnectionRepository.class);
        GitEncryptionService encryptionService = new GitEncryptionService();
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new GitProviderConnectionService(repository, encryptionService, eventPublisher);
    }

    @Test
    void testRefreshConnection() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID connId = UUID.randomUUID();

        GitProviderConnection conn = new GitProviderConnection(orgId, "GITHUB", "cloudforge-org", "enc_token", null, "repo");
        when(repository.findById(connId)).thenReturn(Optional.of(conn));
        when(repository.save(any(GitProviderConnection.class))).thenAnswer(inv -> inv.getArgument(0));

        GitProviderConnectionService.ConnectionResponse response = service.refreshConnection(userId, orgId, connId);

        assertNotNull(response);
        assertEquals("CONNECTED", response.healthStatus());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
