package ai.cloudforge.api.ai.log;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogSummaryServiceTest {

    private LogSummaryService summaryService;

    @BeforeEach
    void setUp() {
        summaryService = new LogSummaryService();
    }

    @Test
    void testGenerateLogSummary() {
        LogEntry entry = new LogEntry(UUID.randomUUID(), "RUNNER", "ERROR", "Runner disconnected", "");
        LogSummaryService.LogSummaryReport report = summaryService.generateLogSummary(List.of(entry));

        assertNotNull(report);
        assertEquals(1, report.logCount());
        assertNotNull(report.executiveSummary());
    }
}
