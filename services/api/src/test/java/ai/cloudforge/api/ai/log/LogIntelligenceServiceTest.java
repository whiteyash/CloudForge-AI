package ai.cloudforge.api.ai.log;

import java.util.Optional;
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
import ai.cloudforge.api.notification.EventPublisher;

class LogIntelligenceServiceTest {

    private LogEntryRepository logRepository;
    private LogAnalysisResultRepository analysisRepository;
    private LogIntelligenceService service;

    @BeforeEach
    void setUp() {
        logRepository = Mockito.mock(LogEntryRepository.class);
        LogClusterRepository clusterRepository = Mockito.mock(LogClusterRepository.class);
        ExceptionFingerprintRepository fingerprintRepository = Mockito.mock(ExceptionFingerprintRepository.class);
        analysisRepository = Mockito.mock(LogAnalysisResultRepository.class);

        IntentResolver intentResolver = new IntentResolver();
        ContextBuilder contextBuilder = new ContextBuilder();
        PromptTemplateEngine promptEngine = new PromptTemplateEngine();
        LLMProvider llmProvider = new MockLLMProvider();
        EvidenceCollector evidenceCollector = new EvidenceCollector();
        RecommendationFormatter recommendationFormatter = new RecommendationFormatter();
        AuditLogger auditLogger = new AuditLogger();
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        service = new LogIntelligenceService(
                logRepository, clusterRepository, fingerprintRepository, analysisRepository,
                intentResolver, contextBuilder, promptEngine, llmProvider,
                evidenceCollector, recommendationFormatter, auditLogger, eventPublisher
        );
    }

    @Test
    void testAnalyzeLogReturnsStructuredAiResponse() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        LogEntry entry = new LogEntry(projectId, "PIPELINE", "ERROR", "NullPointerException in job execution", "at java.lang.NullPointerException");
        when(logRepository.findById(logId)).thenReturn(Optional.of(entry));
        when(analysisRepository.save(any(LogAnalysisResult.class))).thenAnswer(inv -> inv.getArgument(0));

        AIResponse<LogIntelligenceService.LogAnalysisResultResponse> response = service.analyzeLog(orgId, userId, projectId, logId);

        assertNotNull(response);
        assertEquals(92, response.confidence());
        assertNotNull(response.summary());
        assertNotNull(response.payload());
    }
}
