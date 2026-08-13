package ai.cloudforge.api.ai.core;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

class CompositeLLMProviderTest {

    private CompositeLLMProvider compositeLLMProvider;

    @BeforeEach
    void setUp() {
        MockLLMProvider mockLLMProvider = new MockLLMProvider();
        compositeLLMProvider = new CompositeLLMProvider(mockLLMProvider);
    }

    @Test
    void testUnconfiguredProviderThrowsException() {
        // When AI_PROVIDER and AI_API_KEY are unconfigured in test env
        if (!compositeLLMProvider.getProviderName().startsWith("Live-")) {
            AiProviderNotConfiguredException exception = assertThrows(
                    AiProviderNotConfiguredException.class,
                    () -> compositeLLMProvider.generateCompletion("Analyze logs")
            );
            assertTrue(exception.getMessage().contains("no AI provider is configured"));
        }
    }
}
