package ai.cloudforge.api.ai.rca;

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
public class RootCauseAnalysisService {

    private final RootCauseReportRepository reportRepository;
    private final CausalGraphRepository graphRepository;
    private final DependencyLinkRepository linkRepository;
    private final EvidenceRecordRepository evidenceRepository;

    private final IntentResolver intentResolver;
    private final ContextBuilder contextBuilder;
    private final PromptTemplateEngine promptEngine;
    private final LLMProvider llmProvider;
    private final EvidenceCollector evidenceCollector;
    private final RecommendationFormatter recommendationFormatter;
    private final AuditLogger auditLogger;
    private final DependencyGraphService graphService;
    private final ConfidenceEngine confidenceEngine;
    private final RecommendationEngine rcaRecommendationEngine;

    public RootCauseAnalysisService(
            RootCauseReportRepository reportRepository,
            CausalGraphRepository graphRepository,
            DependencyLinkRepository linkRepository,
            EvidenceRecordRepository evidenceRepository,
            IntentResolver intentResolver,
            ContextBuilder contextBuilder,
            PromptTemplateEngine promptEngine,
            LLMProvider llmProvider,
            EvidenceCollector evidenceCollector,
            RecommendationFormatter recommendationFormatter,
            AuditLogger auditLogger,
            DependencyGraphService graphService,
            ConfidenceEngine confidenceEngine,
            RecommendationEngine rcaRecommendationEngine) {
        this.reportRepository = reportRepository;
        this.graphRepository = graphRepository;
        this.linkRepository = linkRepository;
        this.evidenceRepository = evidenceRepository;
        this.intentResolver = intentResolver;
        this.contextBuilder = contextBuilder;
        this.promptEngine = promptEngine;
        this.llmProvider = llmProvider;
        this.evidenceCollector = evidenceCollector;
        this.recommendationFormatter = recommendationFormatter;
        this.auditLogger = auditLogger;
        this.graphService = graphService;
        this.confidenceEngine = confidenceEngine;
        this.rcaRecommendationEngine = rcaRecommendationEngine;
    }

    @Transactional(readOnly = true)
    public List<RootCauseReportResponse> getReportsForProject(UUID projectId) {
        return reportRepository.findByProjectId(projectId).stream()
                .map(RootCauseReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DependencyGraphService.DependencyLinkResponse> getGraphForProject(UUID projectId) {
        return graphService.buildGraph(projectId);
    }

    @Transactional
    public AIResponse<RootCauseReportResponse> performRootCauseAnalysis(UUID orgId, UUID userId, UUID projectId, UUID incidentId) {
        IntentResolver.ResolvedIntent intent = intentResolver.resolveIntent(projectId, userId, "Root cause analysis for incident #" + incidentId);
        ContextBuilder.OperationalContext context = contextBuilder.buildContext(projectId, userId, "INCIDENT");
        String prompt = promptEngine.renderPrompt("ROOT_CAUSE", "Incident #" + incidentId, context.environmentHealth());

        LLMProvider.LLMResult llmResult = llmProvider.generateCompletion(prompt);
        ConfidenceEngine.ConfidenceAssessment assessment = confidenceEngine.evaluateConfidence(0.88, 2);

        auditLogger.logAiOperation(
                projectId, userId, llmProvider.getProviderName(),
                intent.intentType(), llmResult.latencyMs(),
                llmResult.promptTokens(), llmResult.completionTokens(), true
        );

        RootCauseReport report = reportRepository.save(new RootCauseReport(
                projectId,
                incidentId,
                "Root Cause Analysis for Incident #" + incidentId,
                "Cascading failure triggered by OOMKilled runner daemon evicting deployment target pod.",
                assessment.score(),
                assessment.riskRating()
        ));

        List<String> evidence = evidenceCollector.collectEvidence("METRICS", "Pod memory usage reached 98%", "Runner ping timeout at T-2s");
        List<String> recommendations = rcaRecommendationEngine.generateRecommendations("OOM");

        return new AIResponse<>(
                report.getSummary(),
                report.getConfidenceScore(),
                evidence,
                report.getRootCause(),
                recommendations,
                List.of("Resource limit threshold warning"),
                List.of("Incident#" + incidentId),
                RootCauseReportResponse.fromEntity(report)
        );
    }

    public record RootCauseReportResponse(
            UUID id,
            UUID projectId,
            UUID incidentId,
            String summary,
            String rootCause,
            int confidenceScore,
            String riskRating
    ) {
        public static RootCauseReportResponse fromEntity(RootCauseReport r) {
            return new RootCauseReportResponse(r.getId(), r.getProjectId(), r.getIncidentId(), r.getSummary(), r.getRootCause(), r.getConfidenceScore(), r.getRiskRating());
        }
    }
}
