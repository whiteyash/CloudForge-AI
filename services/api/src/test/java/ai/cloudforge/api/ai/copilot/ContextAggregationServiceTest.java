package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.cloudforge.api.ai.core.ContextBuilder;

class ContextAggregationServiceTest {

    private ContextAggregationService aggregationService;

    @BeforeEach
    void setUp() {
        ContextBuilder contextBuilder = new ContextBuilder();
        aggregationService = new ContextAggregationService(contextBuilder);
    }

    @Test
    void testAggregateOperationalContext() {
        ContextAggregationService.UnifiedContext context = aggregationService.aggregateOperationalContext(UUID.randomUUID(), UUID.randomUUID());
        assertNotNull(context);
        assertNotNull(context.environmentHealth());
    }
}
