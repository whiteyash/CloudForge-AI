package ai.cloudforge.api.ai.core;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class IntentResolver {

    public ResolvedIntent resolveIntent(UUID projectId, UUID userId, String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return new ResolvedIntent("UNKNOWN_INTENT", projectId, userId, "General Inquiry");
        }

        String coreQuery = rawInput;
        if (rawInput.startsWith("[Session:") && rawInput.contains("]")) {
            coreQuery = rawInput.substring(rawInput.indexOf("]") + 1).trim();
        }
        if (coreQuery.isBlank()) {
            coreQuery = rawInput;
        }

        String lower = coreQuery.toLowerCase();
        String intentType;
        if (lower.equals("hi") || lower.equals("hii") || lower.equals("hello") || lower.equals("hey") || lower.startsWith("greetings")) {
            intentType = "GREETING_INTENT";
        } else if (lower.contains("802") || lower.contains("incident") || lower.contains("oom") || lower.contains("crash")) {
            intentType = "INCIDENT_ANALYSIS_INTENT";
        } else if (lower.contains("runner") || lower.contains("scale") || lower.contains("capacity") || lower.contains("pool")) {
            intentType = "RUNNER_SCALING_INTENT";
        } else if (lower.contains("brief") || lower.contains("daily") || lower.contains("ops")) {
            intentType = "DAILY_OPS_INTENT";
        } else if (lower.contains("log") || lower.contains("error") || lower.contains("exception")) {
            intentType = "LOG_ANALYSIS_INTENT";
        } else if (lower.contains("cause") || lower.contains("root") || lower.contains("why")) {
            intentType = "ROOT_CAUSE_INTENT";
        } else if (lower.contains("predict") || lower.contains("risk") || lower.contains("forecast")) {
            intentType = "PREDICTIVE_RISK_INTENT";
        } else if (lower.contains("runbook") || lower.contains("playbook") || lower.contains("guide")) {
            intentType = "KNOWLEDGE_RUNBOOK_INTENT";
        } else if (lower.contains("remediate") || lower.contains("rollback") || lower.contains("restart")) {
            intentType = "AUTONOMOUS_REMEDIATION_INTENT";
        } else if (rawInput.toLowerCase().contains("incident")) {
            intentType = "INCIDENT_ANALYSIS_INTENT";
        } else if (rawInput.toLowerCase().contains("runner")) {
            intentType = "RUNNER_SCALING_INTENT";
        } else if (rawInput.toLowerCase().contains("ops")) {
            intentType = "DAILY_OPS_INTENT";
        } else {
            intentType = "COPILOT_CHAT_INTENT";
        }

        return new ResolvedIntent(intentType, projectId, userId, rawInput);
    }

    public record ResolvedIntent(
            String intentType,
            UUID projectId,
            UUID userId,
            String rawInput
    ) {}
}
