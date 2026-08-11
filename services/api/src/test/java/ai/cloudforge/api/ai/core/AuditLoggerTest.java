package ai.cloudforge.api.ai.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditLoggerTest {

    private AuditLogger auditLogger;

    @BeforeEach
    void setUp() {
        auditLogger = new AuditLogger();
    }

    @Test
    void testLogAiOperationDoesNotThrow() {
        assertDoesNotThrow(() -> {
            auditLogger.logAiOperation(
                    UUID.randomUUID(), UUID.randomUUID(), "MockLLMProvider",
                    "LOG_ANALYSIS_INTENT", 45L, 120, 85, true
            );
        });
    }
}
