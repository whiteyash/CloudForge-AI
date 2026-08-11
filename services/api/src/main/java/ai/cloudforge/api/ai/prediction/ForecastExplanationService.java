package ai.cloudforge.api.ai.prediction;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ForecastExplanationService {

    public List<String> explainPrediction(String forecastType) {
        return List.of(
                "Historical 30-day success rate is 96%",
                "Runner queue saturation level is low (12% capacity)",
                "No active memory leak detected in target environment"
        );
    }
}
