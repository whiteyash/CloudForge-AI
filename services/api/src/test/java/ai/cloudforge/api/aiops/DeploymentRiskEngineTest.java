package ai.cloudforge.api.aiops;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.EventPublisher;

class DeploymentRiskEngineTest {

    private DeploymentRiskRepository riskRepository;
    private EventPublisher eventPublisher;
    private DeploymentRiskEngine riskEngine;

    @BeforeEach
    void setUp() {
        riskRepository = Mockito.mock(DeploymentRiskRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        riskEngine = new DeploymentRiskEngine(riskRepository, eventPublisher);
    }

    @Test
    void testEvaluateDeploymentRiskReturnsLowForHealthyState() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();

        when(riskRepository.findByDeploymentId(deploymentId)).thenReturn(Optional.empty());
        when(riskRepository.save(any(DeploymentRiskAssessment.class))).thenAnswer(inv -> inv.getArgument(0));

        DeploymentRiskEngine.RiskAssessmentResponse response = riskEngine.evaluateDeploymentRisk(orgId, userId, projectId, deploymentId);

        assertNotNull(response);
        assertEquals("LOW", response.riskLevel());
        assertEquals(0.91, response.confidenceScore());
    }
}
