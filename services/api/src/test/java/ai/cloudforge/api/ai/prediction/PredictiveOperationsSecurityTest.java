package ai.cloudforge.api.ai.prediction;

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

class PredictiveOperationsSecurityTest {

    private PredictiveForecastRepository forecastRepository;
    private PredictiveOperationsService service;

    @BeforeEach
    void setUp() {
        forecastRepository = Mockito.mock(PredictiveForecastRepository.class);
        CapacityForecastRepository capacityRepository = Mockito.mock(CapacityForecastRepository.class);

        IntentResolver intentResolver = new IntentResolver();
        ContextBuilder contextBuilder = new ContextBuilder();
        PromptTemplateEngine promptEngine = new PromptTemplateEngine();
        LLMProvider llmProvider = new MockLLMProvider();
        EvidenceCollector evidenceCollector = new EvidenceCollector();
        RecommendationFormatter recommendationFormatter = new RecommendationFormatter();
        AuditLogger auditLogger = new AuditLogger();
        DeploymentPredictionService deploymentPredictionService = new DeploymentPredictionService();
        PipelinePredictionService pipelinePredictionService = new PipelinePredictionService();
        CapacityForecastService capacityForecastService = new CapacityForecastService();
        IncidentPredictionService incidentPredictionService = new IncidentPredictionService();
        ForecastExplanationService explanationService = new ForecastExplanationService();

        service = new PredictiveOperationsService(
                forecastRepository, capacityRepository,
                intentResolver, contextBuilder, promptEngine, llmProvider,
                evidenceCollector, recommendationFormatter, auditLogger,
                deploymentPredictionService, pipelinePredictionService,
                capacityForecastService, incidentPredictionService, explanationService
        );
    }

    @Test
    void testTenantScopedPredictiveReportGeneration() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(forecastRepository.save(any(PredictiveForecast.class))).thenAnswer(inv -> inv.getArgument(0));

        AIResponse<PredictiveOperationsService.PredictiveForecastResponse> response = service.generatePredictiveReport(orgId, userId, projectId);
        assertNotNull(response);
    }
}
