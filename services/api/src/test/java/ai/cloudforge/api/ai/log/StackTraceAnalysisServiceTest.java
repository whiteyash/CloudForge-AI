package ai.cloudforge.api.ai.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StackTraceAnalysisServiceTest {

    private StackTraceAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new StackTraceAnalysisService();
    }

    @Test
    void testParseStackTrace() {
        StackTraceAnalysisService.ParsedStackTrace parsed = analysisService.parseStackTrace("java.lang.NullPointerException at ai.cloudforge.PipelineEngine.java:142");
        assertNotNull(parsed);
        assertEquals("java.lang.NullPointerException", parsed.exceptionClass());
        assertEquals(142, parsed.lineNumber());
    }
}
