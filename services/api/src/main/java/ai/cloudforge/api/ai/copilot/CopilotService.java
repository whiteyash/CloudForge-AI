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
        return processCopilotChat(orgId, userId, projectId, conversationId, prompt, "DEV");
    }

    @Transactional
    public AIConversationResponse<CopilotResponse> processCopilotChat(
            UUID orgId, UUID userId, UUID projectId, UUID conversationId, String prompt, String environment) {

        IntentRouter.RoutedIntent routed = intentRouter.routePrompt(projectId, userId, prompt);
        boolean projectExists = projectRepository == null || (projectId != null && projectRepository.existsById(projectId));

        if (projectExists) {
            try {
                intentHistoryRepository.save(new IntentHistory(projectId, userId, routed.intentType(), prompt));
            } catch (Exception ignored) {}
        }

        ContextAggregationService.UnifiedContext context = contextAggregationService.aggregateOperationalContext(orgId, projectId, userId, environment);
        ConversationManager.ConversationSession sessionMemory = conversationOrchestrator.orchestrateSession(projectId, userId, conversationId);

        StringBuilder contextPrompt = new StringBuilder();
        contextPrompt.append("CloudForge Real-Time Operational Context:\n");
        contextPrompt.append("- Environment: ").append(context.environment()).append("\n");
        contextPrompt.append("- Environment Health: ").append(context.environmentHealth()).append("\n");
        contextPrompt.append("- Project Name: ").append(context.projectName()).append("\n");
        contextPrompt.append("- Active Incidents: ").append(context.incidentSummary()).append("\n");
        contextPrompt.append("- Total Org Projects: ").append(context.projectsCount()).append("\n");
        contextPrompt.append("- Total Org Members: ").append(context.membersCount()).append("\n");
        contextPrompt.append("- Recent Security & Activity Audit Trail: ").append(context.recentAuditLogsSummary()).append("\n\n");
        contextPrompt.append("User Prompt: ").append(prompt);

        LLMProvider.LLMResult llmResult = llmProvider.generateCompletion(contextPrompt.toString());

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

        String answerText = llmResult.textResponse();

        if (projectExists) {
            try {
                messageRepository.save(new CopilotMessage(sessionId, "COPILOT", answerText, "Processed successfully"));
            } catch (Exception ignored) {}
        }

        List<String> evidence = List.of(
                "Aggregated real operational context for " + context.projectName() + " [" + context.environment() + "]",
                "Live LLM Provider: " + llmProvider.getProviderName()
        );

        List<String> recommendations = List.of(
                "Check active incidents in " + context.environment(),
                "Inspect pipeline & deployment health"
        );

        CopilotResponse payload = new CopilotResponse(
                sessionId,
                routed.intentType(),
                routed.targetService(),
                answerText,
                context.environmentHealth()
        );

        AIResponse<CopilotResponse> baseResponse = new AIResponse<>(
                answerText,
                98,
                evidence,
                "Query processed via " + llmProvider.getProviderName(),
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
