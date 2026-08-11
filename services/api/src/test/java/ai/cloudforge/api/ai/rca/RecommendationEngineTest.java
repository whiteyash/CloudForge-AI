package ai.cloudforge.api.ai.rca;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecommendationEngineTest {

    private RecommendationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new RecommendationEngine();
    }

    @Test
    void testGenerateOomRecommendations() {
        List<String> recs = engine.generateRecommendations("OOM");
        assertEquals(2, recs.size());
        assertTrue(recs.get(0).contains("Increase runner container memory limit"));
    }
}
