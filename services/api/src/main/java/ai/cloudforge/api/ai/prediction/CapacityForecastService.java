package ai.cloudforge.api.ai.prediction;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CapacityForecastService {

    public List<CapacityForecastResult> forecastCapacity(UUID projectId) {
        return List.of(
                new CapacityForecastResult("STORAGE", 450.5, 890.0, 42),
                new CapacityForecastResult("RUNNER_MEMORY", 78.5, 96.2, 14),
                new CapacityForecastResult("LOG_VOLUME", 12.4, 34.0, 60)
        );
    }

    public record CapacityForecastResult(
            String metricName,
            double currentUsage,
            double projectedUsage,
            int exhaustionDays
    ) {}
}
