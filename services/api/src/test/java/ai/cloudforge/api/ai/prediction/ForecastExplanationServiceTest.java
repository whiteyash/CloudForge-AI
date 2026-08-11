package ai.cloudforge.api.ai.prediction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForecastExplanationServiceTest {

    private ForecastExplanationService service;

    @BeforeEach
    void setUp() {
        service = new ForecastExplanationService();
    }

    @Test
    void testExplainPrediction() {
        List<String> explanations = service.explainPrediction("OPERATIONAL_RISK");
        assertNotNull(explanations);
        assertEquals(3, explanations.size());
    }
}
