package ai.cloudforge.api.ai.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockLLMProviderTest {

    private MockLLMProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockLLMProvider();
    }

    @Test
    void testMockLLMProviderCompletion() {
        assertEquals("MockLLMProvider-v1", provider.getProviderName());
        LLMProvider.LLMResult result = provider.generateCompletion("Analyze pipeline risk");

        assertNotNull(result);
        assertTrue(result.textResponse().contains("Analysis complete"));
        assertTrue(result.latencyMs() > 0);
    }
}
