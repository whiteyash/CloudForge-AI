package ai.cloudforge.api.ai.rca;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.ContextBuilder;
import ai.cloudforge.api.ai.core.EvidenceCollector;
import ai.cloudforge.api.ai.core.IntentResolver;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.MockLLMProvider;
import ai.cloudforge.api.ai.core.PromptTemplateEngine;
import ai.cloudforge.api.ai.core.RecommendationFormatter;

class RootCauseSecurityTest {

    private RootCauseReportRepository reportRepository;
    private RootCauseAnalysisService service;

    @BeforeEach
    void setUp() {
        reportRepository = Mockito.mock(RootCauseReportRepository.class);
        CausalGraphRepository graphRepository = Mockito.mock(CausalGraphRepository.class);
        DependencyLinkRepository linkRepository = Mockito.mock(DependencyLinkRepository.class);
        EvidenceRecordRepository evidenceRepository = Mockito.mock(EvidenceRecordRepository.class);

        IntentResolver intentResolver = new IntentResolver();
        ContextBuilder contextBuilder = new ContextBuilder();
        PromptTemplateEngine promptEngine = new PromptTemplateEngine();
        LLMProvider llmProvider = new MockLLMProvider();
        EvidenceCollector evidenceCollector = new EvidenceCollector();
        RecommendationFormatter recommendationFormatter = new RecommendationFormatter();
        AuditLogger auditLogger = new AuditLogger();
        DependencyGraphService graphService = new DependencyGraphService();
        ConfidenceEngine confidenceEngine = new ConfidenceEngine();
        RecommendationEngine rcaRecommendationEngine = new RecommendationEngine();

        service = new RootCauseAnalysisService(
                reportRepository, graphRepository, linkRepository, evidenceRepository,
                intentResolver, contextBuilder, promptEngine, llmProvider,
                evidenceCollector, recommendationFormatter, auditLogger,
                graphService, confidenceEngine, rcaRecommendationEngine
        );
    }

    @Test
    void testTenantScopedRootCauseAnalysisExecution() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(reportRepository.save(any(RootCauseReport.class))).thenAnswer(inv -> inv.getArgument(0));

        AIResponse<RootCauseAnalysisService.RootCauseReportResponse> response = service.performRootCauseAnalysis(orgId, userId, projectId, null);
        assertNotNull(response);
    }
}
