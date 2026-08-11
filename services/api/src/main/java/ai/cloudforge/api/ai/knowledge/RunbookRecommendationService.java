package ai.cloudforge.api.ai.knowledge;

import org.springframework.stereotype.Service;

@Service
public class RunbookRecommendationService {

    public RecommendedRunbook recommendRunbook(String incidentType) {
        return new RecommendedRunbook(
                "Runbook #102: Kubernetes OOMKilled Container Remediation",
                "INCIDENT",
                "Increase memory allocation & restart evicted runner pod",
                "1.4",
                96,
                92
        );
    }

    public record RecommendedRunbook(
            String title,
            String category,
            String recommendedAction,
            String version,
            int successRate,
            int confidence
    ) {}
}
