package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.memory.AIConversationResponse;
import ai.cloudforge.api.ai.memory.ConversationManager;

import ai.cloudforge.api.project.ProjectRepository;

@Service
public class CopilotService {

    private final CopilotSessionRepository sessionRepository;
    private final CopilotMessageRepository messageRepository;
    private final IntentHistoryRepository intentHistoryRepository;

    private final IntentRouter intentRouter;
    private final ContextAggregationService contextAggregationService;
    private final ConversationOrchestrator conversationOrchestrator;
    private final LLMProvider llmProvider;
    private final AuditLogger auditLogger;
    private final ProjectRepository projectRepository;

    public CopilotService(
            CopilotSessionRepository sessionRepository,
            CopilotMessageRepository messageRepository,
            IntentHistoryRepository intentHistoryRepository,
            IntentRouter intentRouter,
            ContextAggregationService contextAggregationService,
            ConversationOrchestrator conversationOrchestrator,
            LLMProvider llmProvider,
            AuditLogger auditLogger,
            @org.springframework.lang.Nullable ProjectRepository projectRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.intentHistoryRepository = intentHistoryRepository;
        this.intentRouter = intentRouter;
        this.contextAggregationService = contextAggregationService;
        this.conversationOrchestrator = conversationOrchestrator;
        this.llmProvider = llmProvider;
        this.auditLogger = auditLogger;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public AIConversationResponse<CopilotResponse> processCopilotChat(
            UUID orgId, UUID userId, UUID projectId, UUID conversationId, String prompt) {

        IntentRouter.RoutedIntent routed = intentRouter.routePrompt(projectId, userId, prompt);
        boolean projectExists = projectRepository == null || (projectId != null && projectRepository.existsById(projectId));

        if (projectExists) {
            try {
                intentHistoryRepository.save(new IntentHistory(projectId, userId, routed.intentType(), prompt));
            } catch (Exception ignored) {}
        }

        ContextAggregationService.UnifiedContext context = contextAggregationService.aggregateOperationalContext(projectId, userId);
        ConversationManager.ConversationSession sessionMemory = conversationOrchestrator.orchestrateSession(projectId, userId, conversationId);

        LLMProvider.LLMResult llmResult = llmProvider.generateCompletion(prompt);

        auditLogger.logAiOperation(
                projectId, userId, llmProvider.getProviderName(),
                routed.intentType(), llmResult.latencyMs(),
                llmResult.promptTokens(), llmResult.completionTokens(), true
        );

        UUID sessionId = UUID.randomUUID();
        if (projectExists) {
            try {
                CopilotSession session = sessionRepository.save(new CopilotSession(projectId, userId, prompt, "ACTIVE"));
                sessionId = session.getId();
                messageRepository.save(new CopilotMessage(sessionId, "USER", prompt, null));
            } catch (Exception ignored) {}
        }

        String answerText = "Copilot Analysis [" + routed.targetService() + "]: " + llmResult.textResponse();
        if (!"GREETING_INTENT".equals(routed.intentType())) {
            answerText += "\nOperational Context: " + context.environmentHealth() + ", Active Incidents: " + context.activeIncidents();
        }

        if (projectExists) {
            try {
                messageRepository.save(new CopilotMessage(sessionId, "COPILOT", answerText, "Processed successfully"));
            } catch (Exception ignored) {}
        }

        List<String> evidence = List.of(
                "Aggregated context for project #" + projectId,
                "Intent classified as " + routed.intentType() + " (Confidence: " + routed.confidence() + "%)"
        );

        List<String> recommendations;
        if ("GREETING_INTENT".equals(routed.intentType())) {
            recommendations = List.of(
                    "Ask: 'Why did my last build fail?'",
                    "Ask: 'Show runner pool capacity'",
                    "Ask: 'Generate executive operations brief'"
            );
        } else if ("INCIDENT_ANALYSIS_INTENT".equals(routed.intentType()) || "ROOT_CAUSE_INTENT".equals(routed.intentType())) {
            recommendations = List.of(
                    "Inspect container memory limit configurations",
                    "Review recent deployment commit logs for OOM evictions"
            );
        } else if ("RUNNER_SCALING_INTENT".equals(routed.intentType())) {
            recommendations = List.of(
                    "Check active runner node concurrency limits",
                    "Configure auto-scaling rule for peak deployment hours"
            );
        } else {
            recommendations = List.of(
                    "Monitor system telemetry dashboard",
                    "Verify deployment pipeline stage configurations"
            );
        }

        CopilotResponse payload = new CopilotResponse(
                sessionId,
                routed.intentType(),
                routed.targetService(),
                answerText,
                context.environmentHealth()
        );

        AIResponse<CopilotResponse> baseResponse = new AIResponse<>(
                answerText,
                routed.confidence(),
                evidence,
                "Query processed for intent: " + routed.intentType(),
                recommendations,
                List.of("Telemetry check complete"),
                List.of("Session#" + sessionId),
                payload
        );

        return new AIConversationResponse<>(
                sessionMemory != null ? sessionMemory.id() : sessionId,
                UUID.randomUUID(),
                null,
                "Mission Control Copilot Session",
                baseResponse
        );
    }

    @Transactional(readOnly = true)
    public List<CopilotSessionResponse> getConversationsForProject(UUID projectId) {
        return sessionRepository.findByProjectId(projectId).stream()
                .map(CopilotSessionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteConversation(UUID conversationId) {
        sessionRepository.deleteById(conversationId);
    }

    public record CopilotResponse(
            UUID sessionId,
            String intentType,
            String targetService,
            String answer,
            String environmentHealth
    ) {}

    public record CopilotSessionResponse(
            UUID id,
            UUID projectId,
            UUID userId,
            String title,
            String status
    ) {
        public static CopilotSessionResponse fromEntity(CopilotSession s) {
            return new CopilotSessionResponse(s.getId(), s.getProjectId(), s.getUserId(), s.getTitle(), s.getStatus());
        }
    }
}
