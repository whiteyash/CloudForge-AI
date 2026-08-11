package ai.cloudforge.api.ai.prediction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentPredictionServiceTest {

    private IncidentPredictionService service;

    @BeforeEach
    void setUp() {
        service = new IncidentPredictionService();
    }

    @Test
    void testPredictIncidentLikelihood() {
        IncidentPredictionService.IncidentPredictionResult result = service.predictIncidentLikelihood("runner-pool");
        assertNotNull(result);
        assertEquals(15, result.likelihoodPercent());
        assertEquals("LOW", result.severity());
    }
}
