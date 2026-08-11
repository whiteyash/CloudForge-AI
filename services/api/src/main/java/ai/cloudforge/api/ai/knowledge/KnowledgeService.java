package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.ContextBuilder;
import ai.cloudforge.api.ai.core.EvidenceCollector;
import ai.cloudforge.api.ai.core.IntentResolver;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.PromptTemplateEngine;
import ai.cloudforge.api.ai.core.RecommendationFormatter;

@Service
public class KnowledgeService {

    private final AIRunbookRepository runbookRepository;
    private final PostmortemRepository postmortemRepository;

    private final IntentResolver intentResolver;
    private final ContextBuilder contextBuilder;
    private final PromptTemplateEngine promptEngine;
    private final LLMProvider llmProvider;
    private final EvidenceCollector evidenceCollector;
    private final RecommendationFormatter recommendationFormatter;
    private final AuditLogger auditLogger;
    private final KnowledgeSearchService searchService;
    private final RunbookRecommendationService recommendationService;
    private final IncidentSimilarityService similarityService;
    private final PostmortemService postmortemService;
    private final KnowledgeGraphService graphService;

    public KnowledgeService(
            AIRunbookRepository runbookRepository,
            PostmortemRepository postmortemRepository,
            IntentResolver intentResolver,
            ContextBuilder contextBuilder,
            PromptTemplateEngine promptEngine,
            LLMProvider llmProvider,
            EvidenceCollector evidenceCollector,
            RecommendationFormatter recommendationFormatter,
            AuditLogger auditLogger,
            KnowledgeSearchService searchService,
            RunbookRecommendationService recommendationService,
            IncidentSimilarityService similarityService,
            PostmortemService postmortemService,
            KnowledgeGraphService graphService) {
        this.runbookRepository = runbookRepository;
        this.postmortemRepository = postmortemRepository;
        this.intentResolver = intentResolver;
        this.contextBuilder = contextBuilder;
        this.promptEngine = promptEngine;
        this.llmProvider = llmProvider;
        this.evidenceCollector = evidenceCollector;
        this.recommendationFormatter = recommendationFormatter;
        this.auditLogger = auditLogger;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
        this.similarityService = similarityService;
        this.postmortemService = postmortemService;
        this.graphService = graphService;
    }

    @Transactional(readOnly = true)
    public List<AIRunbookResponse> getRunbooksForProject(UUID projectId) {
        return runbookRepository.findByProjectId(projectId).stream()
                .map(AIRunbookResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<KnowledgeSearchService.SearchResult> searchKnowledge(UUID projectId, String query) {
        return searchService.searchKnowledge(projectId, query);
    }

    @Transactional(readOnly = true)
    public List<IncidentSimilarityService.SimilarIncidentResult> getSimilarIncidents(UUID projectId, UUID incidentId) {
        return similarityService.findSimilarIncidents(projectId, incidentId);
    }

    @Transactional(readOnly = true)
    public List<KnowledgeGraphService.GraphLink> getKnowledgeGraph(UUID projectId) {
        return graphService.buildKnowledgeGraph(projectId);
    }

    @Transactional
    public AIResponse<PostmortemResponse> generatePostmortemReport(UUID orgId, UUID userId, UUID projectId, UUID incidentId) {
        IntentResolver.ResolvedIntent intent = intentResolver.resolveIntent(projectId, userId, "Generate postmortem report for incident #" + incidentId);
        ContextBuilder.OperationalContext context = contextBuilder.buildContext(projectId, userId, "INCIDENT");
        String prompt = promptEngine.renderPrompt("POSTMORTEM", "Incident #" + incidentId, context.environmentHealth());

        LLMProvider.LLMResult llmResult = llmProvider.generateCompletion(prompt);

        auditLogger.logAiOperation(
                projectId, userId, llmProvider.getProviderName(),
                intent.intentType(), llmResult.latencyMs(),
                llmResult.promptTokens(), llmResult.completionTokens(), true
        );

        PostmortemService.GeneratedPostmortem generated = postmortemService.generatePostmortem(projectId, incidentId);
        Postmortem entity = postmortemRepository.save(new Postmortem(
                projectId,
                incidentId,
                generated.summary(),
                generated.rootCause(),
                generated.lessonsLearned()
        ));

        List<String> evidence = evidenceCollector.collectEvidence("INCIDENT_HISTORY", "Incident #" + incidentId, "Resolved in 18 minutes");
        List<String> recommendations = recommendationFormatter.formatRecommendations(List.of(
                new RecommendationFormatter.FormattedRecommendation("Attach Runbook #102 to Incident #INC-802", "Provides proven resolution steps", 96)
        ));

        return new AIResponse<>(
                entity.getSummary(),
                96,
                evidence,
                entity.getRootCause(),
                recommendations,
                List.of("Verify follow-up task completion"),
                List.of("Postmortem#" + entity.getId()),
                PostmortemResponse.fromEntity(entity)
        );
    }

    public record AIRunbookResponse(
            UUID id,
            UUID projectId,
            String title,
            String category,
            String content,
            String version,
            int successRate
    ) {
        public static AIRunbookResponse fromEntity(AIRunbook r) {
            return new AIRunbookResponse(r.getId(), r.getProjectId(), r.getTitle(), r.getCategory(), r.getContent(), r.getVersion(), r.getSuccessRate());
        }
    }

    public record PostmortemResponse(
            UUID id,
            UUID projectId,
            UUID incidentId,
            String summary,
            String rootCause,
            String lessonsLearned
    ) {
        public static PostmortemResponse fromEntity(Postmortem p) {
            return new PostmortemResponse(p.getId(), p.getProjectId(), p.getIncidentId(), p.getSummary(), p.getRootCause(), p.getLessonsLearned());
        }
    }
}
