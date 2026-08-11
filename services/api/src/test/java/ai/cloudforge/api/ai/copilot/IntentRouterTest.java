package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.cloudforge.api.ai.core.IntentResolver;

class IntentRouterTest {

    private IntentRouter router;

    @BeforeEach
    void setUp() {
        IntentResolver intentResolver = new IntentResolver();
        router = new IntentRouter(intentResolver);
    }

    @Test
    void testRoutePromptLogAnalysis() {
        IntentRouter.RoutedIntent routed = router.routePrompt(UUID.randomUUID(), UUID.randomUUID(), "Analyze log stack trace");
        assertNotNull(routed);
        assertEquals("LogIntelligenceService", routed.targetService());
    }
}
