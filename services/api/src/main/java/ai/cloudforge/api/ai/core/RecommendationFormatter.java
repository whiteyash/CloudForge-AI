package ai.cloudforge.api.ai.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RecommendationFormatter {

    public List<String> formatRecommendations(List<FormattedRecommendation> recommendations) {
        List<String> formatted = new ArrayList<>();
        if (recommendations != null) {
            for (FormattedRecommendation r : recommendations) {
                formatted.add("ACTION: " + r.action() + " | REASON: " + r.reasoning() + " (Confidence: " + r.confidenceScore() + "%)");
            }
        }
        return formatted;
    }

    public record FormattedRecommendation(
            String action,
            String reasoning,
            int confidenceScore
    ) {}
}
