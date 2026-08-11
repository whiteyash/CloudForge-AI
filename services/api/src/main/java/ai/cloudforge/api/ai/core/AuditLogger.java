package ai.cloudforge.api.ai.core;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);

    public void logAiOperation(
            UUID projectId,
            UUID userId,
            String providerName,
            String intentType,
            long latencyMs,
            int promptTokens,
            int completionTokens,
            boolean success) {

        log.info("AI_AUDIT | project={} | user={} | provider={} | intent={} | latency={}ms | tokens={}+{} | success={}",
                projectId, userId, providerName, intentType, latencyMs, promptTokens, completionTokens, success);
    }
}
