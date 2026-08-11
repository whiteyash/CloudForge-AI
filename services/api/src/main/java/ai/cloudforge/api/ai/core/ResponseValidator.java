package ai.cloudforge.api.ai.core;

import org.springframework.stereotype.Component;

@Component
public class ResponseValidator {

    public <T> boolean isValid(AIResponse<T> response) {
        if (response == null) {
            return false;
        }
        if (response.summary() == null || response.summary().isBlank()) {
            return false;
        }
        if (response.confidence() < 0 || response.confidence() > 100) {
            return false;
        }
        return response.reasoning() != null && !response.reasoning().isBlank();
    }
}
