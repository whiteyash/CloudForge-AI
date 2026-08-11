package ai.cloudforge.api.ai.prediction;

import org.springframework.stereotype.Service;

@Service
public class PipelinePredictionService {

    public PipelinePredictionResult predictPipelineOutcome(String pipelineName) {
        return new PipelinePredictionResult(91, 9, 120, 15);
    }

    public record PipelinePredictionResult(
            int successRate,
            int failureProbability,
            int expectedCompletionTimeSeconds,
            int expectedQueueTimeSeconds
    ) {}
}
