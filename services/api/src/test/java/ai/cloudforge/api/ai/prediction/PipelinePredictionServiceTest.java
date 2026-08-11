package ai.cloudforge.api.ai.prediction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PipelinePredictionServiceTest {

    private PipelinePredictionService service;

    @BeforeEach
    void setUp() {
        service = new PipelinePredictionService();
    }

    @Test
    void testPredictPipelineOutcome() {
        PipelinePredictionService.PipelinePredictionResult result = service.predictPipelineOutcome("MainBuild");
        assertNotNull(result);
        assertEquals(91, result.successRate());
        assertEquals(120, result.expectedCompletionTimeSeconds());
    }
}
