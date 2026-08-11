package ai.cloudforge.api.ai.core;

import org.springframework.stereotype.Component;

@Component
public class ConfidenceCalculator {

    public int calculateConfidence(double rawScore, int evidenceCount, boolean hasDirectRootCause) {
        int baseScore = (int) Math.round(rawScore * 100);
        if (evidenceCount > 2) {
            baseScore += 5;
        }
        if (hasDirectRootCause) {
            baseScore += 5;
        }
        return Math.min(100, Math.max(0, baseScore));
    }
}
