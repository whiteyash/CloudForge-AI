package ai.cloudforge.api.git;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ai.cloudforge.api.notification.EventPublisher;

class OAuthStateValidationTest {

    private GitProviderConnectionService service;

    @BeforeEach
    void setUp() {
        GitProviderConnectionRepository repository = Mockito.mock(GitProviderConnectionRepository.class);
        GitEncryptionService encryptionService = new GitEncryptionService();
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new GitProviderConnectionService(repository, encryptionService, eventPublisher);
    }

    @Test
    void testGitHubAuthUrlGenerationWithState() {
        UUID orgId = UUID.randomUUID();
        String url = service.generateAuthUrl(orgId, "GITHUB");
        assertNotNull(url);
        assertTrue(url.contains("github.com/login/oauth/authorize"));
        assertTrue(url.contains("state="));
        assertTrue(url.contains("scope=repo,read:org"));
    }

    @Test
    void testGitLabAuthUrlGenerationWithState() {
        UUID orgId = UUID.randomUUID();
        String url = service.generateAuthUrl(orgId, "GITLAB");
        assertNotNull(url);
        assertTrue(url.contains("gitlab.com/oauth/authorize"));
        assertTrue(url.contains("state="));
    }
}
