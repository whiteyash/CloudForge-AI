package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.cloudforge.api.ai.core.IntentResolver;

@Service
public class IntentRouter {

    private final IntentResolver intentResolver;

    public IntentRouter(IntentResolver intentResolver) {
        this.intentResolver = intentResolver;
    }

    public RoutedIntent routePrompt(UUID projectId, UUID userId, String prompt) {
        IntentResolver.ResolvedIntent resolved = intentResolver.resolveIntent(projectId, userId, prompt);
        String targetService = determineTargetService(resolved.intentType());
        return new RoutedIntent(resolved.intentType(), targetService, 95);
    }

    private String determineTargetService(String intentType) {
        return switch (intentType) {
            case "GREETING_INTENT" -> "CopilotAssistantService";
            case "INCIDENT_ANALYSIS_INTENT" -> "RootCauseAnalysisService";
            case "RUNNER_SCALING_INTENT" -> "RunnerPlatformService";
            case "DAILY_OPS_INTENT" -> "ObservabilityService";
            case "LOG_ANALYSIS_INTENT" -> "LogIntelligenceService";
            case "ROOT_CAUSE_INTENT" -> "RootCauseAnalysisService";
            case "PREDICTIVE_RISK_INTENT" -> "PredictiveOperationsService";
            case "KNOWLEDGE_RUNBOOK_INTENT" -> "KnowledgeService";
            default -> "ObservabilityService";
        };
    }

    public record RoutedIntent(
            String intentType,
            String targetService,
            int confidence
    ) {}
}
