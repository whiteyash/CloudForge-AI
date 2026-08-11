package ai.cloudforge.api.ai.rca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfidenceEngineTest {

    private ConfidenceEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ConfidenceEngine();
    }

    @Test
    void testEvaluateConfidence() {
        ConfidenceEngine.ConfidenceAssessment assessment = engine.evaluateConfidence(0.85, 1);
        assertEquals(95, assessment.score());
        assertEquals("HIGH", assessment.riskRating());
    }
}
