package ai.cloudforge.api.ai.core;

public interface LLMProvider {
    String getProviderName();
    LLMResult generateCompletion(String prompt);

    record LLMResult(
            String textResponse,
            int promptTokens,
            int completionTokens,
            long latencyMs
    ) {}
}
