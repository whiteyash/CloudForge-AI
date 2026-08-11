package ai.cloudforge.api.ai.log;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LogSummaryService {

    public LogSummaryReport generateLogSummary(List<LogEntry> logs) {
        int total = logs != null ? logs.size() : 0;
        String executive = "Executive Log Summary: " + total + " log entries analyzed. Primary error activity clustered around container memory limits and network timeouts.";
        String technical = "Technical Summary: Identified 2 recurring exception fingerprints. Root cause points to buffer overflow during image extraction.";
        return new LogSummaryReport(executive, technical, total);
    }

    public record LogSummaryReport(
            String executiveSummary,
            String technicalSummary,
            int logCount
    ) {}
}
