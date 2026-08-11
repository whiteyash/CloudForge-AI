package ai.cloudforge.api.ai.rca;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RecommendationEngine {

    public List<String> generateRecommendations(String rootCauseCategory) {
        return switch (rootCauseCategory != null ? rootCauseCategory.toUpperCase() : "GENERAL") {
            case "OOM", "MEMORY" -> List.of("ACTION: Increase runner container memory limit to 4GB", "ACTION: Inspect heap allocation in job stage");
            case "TIMEOUT", "LATENCY" -> List.of("ACTION: Increase database connection pool timeout", "ACTION: Verify VPC network latency");
            default -> List.of("ACTION: Roll back to last known healthy deployment", "ACTION: Restart unhealthy runner pool");
        };
    }
}
