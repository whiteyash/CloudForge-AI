package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.cloudforge.api.project.ProjectRepository;

class ContextAggregationServiceTest {

    private ContextAggregationService aggregationService;

    @BeforeEach
    void setUp() {
        aggregationService = new ContextAggregationService((ProjectRepository) null, null, null, null);
    }

    @Test
    void testAggregateOperationalContext() {
        ContextAggregationService.UnifiedContext context = aggregationService.aggregateOperationalContext(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "DEV");
        assertNotNull(context);
        assertNotNull(context.environmentHealth());
    }
}
