package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class EventPublisherIntegrationTest {

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
    void testEventPublishedOnConnect() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        when(repository.findByOrgIdAndProviderNameAndAccountName(orgId, "GITHUB", "cloudforge-org"))
                .thenReturn(Optional.empty());
        when(repository.save(any(GitProviderConnection.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.connectProvider(userId, orgId, "GITHUB", "cloudforge-org", "token123", null, "repo");

        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testEventPublishedOnDisconnect() {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        UUID connId = UUID.randomUUID();

        GitProviderConnection conn = new GitProviderConnection(orgId, "GITHUB", "cloudforge-org", "enc_token", null, "repo");
        when(repository.findById(connId)).thenReturn(Optional.of(conn));

        service.disconnectProvider(userId, orgId, connId);

        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
