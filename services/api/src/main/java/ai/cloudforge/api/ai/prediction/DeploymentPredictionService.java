package ai.cloudforge.api.ai.prediction;

import org.springframework.stereotype.Service;

@Service
public class DeploymentPredictionService {

    public DeploymentPredictionResult predictDeploymentSuccess(String deploymentTarget) {
        return new DeploymentPredictionResult(94, 6, 45, "LOW");
    }

    public record DeploymentPredictionResult(
            int successProbability,
            int failureProbability,
            int estimatedDurationSeconds,
            String rollbackRisk
    ) {}
}
