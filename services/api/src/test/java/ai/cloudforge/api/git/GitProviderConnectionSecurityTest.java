package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class GitProviderConnectionSecurityTest {

    private GitProviderConnectionRepository repository;
    private GitEncryptionService encryptionService;
    private GitProviderConnectionService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(GitProviderConnectionRepository.class);
        encryptionService = new GitEncryptionService();
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new GitProviderConnectionService(repository, encryptionService, eventPublisher);
    }

    @Test
    void testTenantIsolationCrossTenantAccessBlocked() {
        UUID userId = UUID.randomUUID();
        UUID orgA = UUID.randomUUID();
        UUID orgB = UUID.randomUUID();
        UUID connId = UUID.randomUUID();

        // Connection belongs to orgA
        GitProviderConnection connectionOrgA = new GitProviderConnection(orgA, "GITHUB", "cloudforge-org", "token", null, "repo");
        when(repository.findById(connId)).thenReturn(Optional.of(connectionOrgA));

        // User attempts disconnect from orgB context -> must throw ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            service.disconnectProvider(userId, orgB, connId);
        });
    }

    @Test
    void testTokenMaskingAndEncryptedPersistence() {
        String rawToken = "TEST_GITHUB_TOKEN_PLACEHOLDER";
        String encrypted = encryptionService.encrypt(rawToken);

        // Ensure raw token is not visible in ciphertext
        assertFalse(encrypted.contains(rawToken));
        assertNotNull(encrypted);
        assertTrue(encrypted.length() > 0);
    }
}
