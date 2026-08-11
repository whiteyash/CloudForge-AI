package ai.cloudforge.api.ai.log;

import org.springframework.stereotype.Service;

@Service
public class ErrorClusterService {

    public String classifyErrorCluster(String logMessage, String stackTrace) {
        String combined = (logMessage + " " + (stackTrace != null ? stackTrace : "")).toLowerCase();
        if (combined.contains("nullpointer")) {
            return "NullPointerException";
        } else if (combined.contains("timeout") || combined.contains("timed out")) {
            return "TimeoutException";
        } else if (combined.contains("connection") || combined.contains("database") || combined.contains("sql")) {
            return "DatabaseError";
        } else if (combined.contains("network") || combined.contains("socket") || combined.contains("connect")) {
            return "NetworkFailure";
        } else if (combined.contains("memory") || combined.contains("oom") || combined.contains("heap")) {
            return "MemoryError";
        } else {
            return "GeneralException";
        }
    }
}
