package ai.cloudforge.api.ai.prediction;

import org.springframework.stereotype.Service;

@Service
public class IncidentPredictionService {

    public IncidentPredictionResult predictIncidentLikelihood(String componentName) {
        return new IncidentPredictionResult(15, "LOW", "runner-us-east-1a", 15);
    }

    public record IncidentPredictionResult(
            int likelihoodPercent,
            String severity,
            String affectedComponent,
            int estimatedRecoveryMinutes
    ) {}
}
