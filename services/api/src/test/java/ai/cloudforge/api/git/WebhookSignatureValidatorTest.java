package ai.cloudforge.api.git;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhookSignatureValidatorTest {

    private WebhookSignatureValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WebhookSignatureValidator();
    }

    @Test
    void testGitLabTokenValidation() {
        String secret = "my_gitlab_webhook_secret_123";
        assertTrue(validator.validateSignature("{}", secret, secret, "GITLAB"));
        assertFalse(validator.validateSignature("{}", "wrong_secret", secret, "GITLAB"));
    }

    @Test
    void testGitHubHmacSha256Validation() {
        String payload = "{\"action\":\"published\"}";
        String secret = "secret123";
        // Calculate valid signature for secret123 + payload
        // hmac-sha256 of payload with secret123
        String invalidSignature = "sha256=invalid_hash";

        assertFalse(validator.validateSignature(payload, invalidSignature, secret, "GITHUB"));
    }
}
