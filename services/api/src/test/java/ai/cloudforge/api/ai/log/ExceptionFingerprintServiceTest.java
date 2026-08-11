package ai.cloudforge.api.ai.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExceptionFingerprintServiceTest {

    private ExceptionFingerprintService fingerprintService;

    @BeforeEach
    void setUp() {
        fingerprintService = new ExceptionFingerprintService();
    }

    @Test
    void testGenerateFingerprintHashIsDeterministic() {
        String hash1 = fingerprintService.generateFingerprintHash("NullPointerException", "runJob", "JobEngine.java", 42);
        String hash2 = fingerprintService.generateFingerprintHash("NullPointerException", "runJob", "JobEngine.java", 42);

        assertNotNull(hash1);
        assertEquals(hash1, hash2);
    }
}
