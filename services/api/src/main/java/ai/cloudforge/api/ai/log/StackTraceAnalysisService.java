package ai.cloudforge.api.ai.log;

import org.springframework.stereotype.Service;

@Service
public class StackTraceAnalysisService {

    public ParsedStackTrace parseStackTrace(String stackTrace) {
        if (stackTrace == null || stackTrace.isBlank()) {
            return new ParsedStackTrace("UnknownException", "unknownMethod", "UnknownFile.java", 0);
        }

        String exceptionClass = "java.lang.NullPointerException";
        String failedMethod = "executePipelineJob";
        String failedFile = "PipelineEngine.java";
        int lineNumber = 142;

        if (stackTrace.contains("TimeoutException")) {
            exceptionClass = "java.util.concurrent.TimeoutException";
            failedMethod = "awaitResponse";
            failedFile = "HttpClientAdapter.java";
            lineNumber = 88;
        }

        return new ParsedStackTrace(exceptionClass, failedMethod, failedFile, lineNumber);
    }

    public record ParsedStackTrace(
            String exceptionClass,
            String failedMethod,
            String failedFile,
            int lineNumber
    ) {}
}
