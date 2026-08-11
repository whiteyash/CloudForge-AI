package ai.cloudforge.api.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GitEncryptionServiceTest {

    private GitEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new GitEncryptionService();
    }

    @Test
    void testEncryptAndDecrypt() {
        String token = "ghp_1234567890abcdefghijklmnopqrstuvwxyz";
        String encrypted = encryptionService.encrypt(token);

        assertNotNull(encrypted);
        assertTrue(encrypted.length() > 0);

        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(token, decrypted);
    }

    @Test
    void testNullOrEmptyHandling() {
        assertNull(encryptionService.encrypt(null));
        assertEquals("", encryptionService.encrypt(""));
        assertNull(encryptionService.decrypt(null));
        assertEquals("", encryptionService.decrypt(""));
    }
}
