package ai.cloudforge.api.ai.prediction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeploymentPredictionServiceTest {

    private DeploymentPredictionService service;

    @BeforeEach
    void setUp() {
        service = new DeploymentPredictionService();
    }

    @Test
    void testPredictDeploymentSuccess() {
        DeploymentPredictionService.DeploymentPredictionResult result = service.predictDeploymentSuccess("Staging");
        assertNotNull(result);
        assertEquals(94, result.successProbability());
        assertEquals("LOW", result.rollbackRisk());
    }
}
