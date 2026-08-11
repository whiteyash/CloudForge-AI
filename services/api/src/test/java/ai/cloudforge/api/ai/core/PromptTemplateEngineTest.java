package ai.cloudforge.api.ai.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PromptTemplateEngineTest {

    private PromptTemplateEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PromptTemplateEngine();
    }

    @Test
    void testRenderLogAnalysisPrompt() {
        String prompt = engine.renderPrompt("LOG_ANALYSIS", "StackOverflowError in job #44", "Environment: STAGING");
        assertNotNull(prompt);
        assertTrue(prompt.contains("Analyze stack traces"));
        assertTrue(prompt.contains("StackOverflowError"));
    }
}
