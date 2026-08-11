package ai.cloudforge.api.ai.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntentResolverTest {

    private IntentResolver intentResolver;

    @BeforeEach
    void setUp() {
        intentResolver = new IntentResolver();
    }

    @Test
    void testResolveLogIntent() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        IntentResolver.ResolvedIntent intent = intentResolver.resolveIntent(projectId, userId, "Analyze the pipeline error log");

        assertNotNull(intent);
        assertEquals("LOG_ANALYSIS_INTENT", intent.intentType());
        assertEquals(projectId, intent.projectId());
    }

    @Test
    void testResolveRootCauseIntent() {
        IntentResolver.ResolvedIntent intent = intentResolver.resolveIntent(UUID.randomUUID(), UUID.randomUUID(), "What is the root cause?");
        assertEquals("ROOT_CAUSE_INTENT", intent.intentType());
    }
}
