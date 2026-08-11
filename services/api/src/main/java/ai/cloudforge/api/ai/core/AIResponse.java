package ai.cloudforge.api.ai.core;

import java.util.List;

public record AIResponse<T>(
        String summary,
        int confidence, // 0 - 100
        List<String> evidence,
        String reasoning,
        List<String> recommendations,
        List<String> warnings,
        List<String> references,
        T payload
) {
    public AIResponse {
        evidence = evidence != null ? List.copyOf(evidence) : List.of();
        recommendations = recommendations != null ? List.copyOf(recommendations) : List.of();
        warnings = warnings != null ? List.copyOf(warnings) : List.of();
        references = references != null ? List.copyOf(references) : List.of();
    }
}
