package ai.cloudforge.api.ai.rca;

import org.springframework.stereotype.Service;

@Service
public class ConfidenceEngine {

    public ConfidenceAssessment evaluateConfidence(double evidenceWeight, int historicalMatches) {
        int score = (int) Math.round(evidenceWeight * 100);
        if (historicalMatches > 0) {
            score += 10;
        }
        score = Math.min(100, Math.max(0, score));
        String rating = score >= 80 ? "HIGH" : score >= 50 ? "MEDIUM" : "LOW";
        return new ConfidenceAssessment(score, rating);
    }

    public record ConfidenceAssessment(
            int score,
            String riskRating
    ) {}
}
