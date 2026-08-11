package ai.cloudforge.api.ai.knowledge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RunbookRecommendationServiceTest {

    private RunbookRecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RunbookRecommendationService();
    }

    @Test
    void testRecommendRunbook() {
        RunbookRecommendationService.RecommendedRunbook runbook = service.recommendRunbook("OOMKilled");
        assertNotNull(runbook);
        assertEquals(96, runbook.successRate());
        assertEquals("1.4", runbook.version());
    }
}
