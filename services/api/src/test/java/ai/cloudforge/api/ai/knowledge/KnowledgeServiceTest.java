package ai.cloudforge.api.ai.knowledge;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class KnowledgeServiceTest {

    private PostmortemRepository postmortemRepository;
    private KnowledgeService service;

    @BeforeEach
    void setUp() {
        AIRunbookRepository runbookRepository = Mockito.mock(AIRunbookRepository.class);
        postmortemRepository = Mockito.mock(PostmortemRepository.class);

        IntentResolver intentResolver = new IntentResolver();
        ContextBuilder contextBuilder = new ContextBuilder();
        PromptTemplateEngine promptEngine = new PromptTemplateEngine();
        LLMProvider llmProvider = new MockLLMProvider();
        EvidenceCollector evidenceCollector = new EvidenceCollector();
        RecommendationFormatter recommendationFormatter = new RecommendationFormatter();
        AuditLogger auditLogger = new AuditLogger();
        KnowledgeSearchService searchService = new KnowledgeSearchService();
        RunbookRecommendationService recommendationService = new RunbookRecommendationService();
        IncidentSimilarityService similarityService = new IncidentSimilarityService();
        PostmortemService postmortemService = new PostmortemService();
        KnowledgeGraphService graphService = new KnowledgeGraphService();

        service = new KnowledgeService(
                runbookRepository, postmortemRepository,
                intentResolver, contextBuilder, promptEngine, llmProvider,
                evidenceCollector, recommendationFormatter, auditLogger,
                searchService, recommendationService, similarityService,
                postmortemService, graphService
        );
    }

    @Test
    void testGeneratePostmortemReportReturnsAIResponse() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID incidentId = UUID.randomUUID();

        when(postmortemRepository.save(any(Postmortem.class))).thenAnswer(inv -> inv.getArgument(0));

        AIResponse<KnowledgeService.PostmortemResponse> response = service.generatePostmortemReport(orgId, userId, projectId, incidentId);

        assertNotNull(response);
        assertEquals(96, response.confidence());
        assertNotNull(response.summary());
        assertNotNull(response.payload());
    }
}
