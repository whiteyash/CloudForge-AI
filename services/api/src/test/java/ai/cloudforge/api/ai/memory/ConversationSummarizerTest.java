package ai.cloudforge.api.ai.memory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationSummarizerTest {

    private ConversationSummarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new ConversationSummarizer();
    }

    @Test
    void testSummarizeHistory() {
        String summary = summarizer.summarizeHistory(List.of("Msg 1", "Msg 2"));
        assertNotNull(summary);
        assertTrue(summary.contains("2 turns"));
    }
}
