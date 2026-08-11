package ai.cloudforge.api.ai.log;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.ContextBuilder;
import ai.cloudforge.api.ai.core.EvidenceCollector;
import ai.cloudforge.api.ai.core.IntentResolver;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.MockLLMProvider;
import ai.cloudforge.api.ai.core.PromptTemplateEngine;
import ai.cloudforge.api.ai.core.RecommendationFormatter;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class LogIntelligenceSecurityTest {

    private LogEntryRepository logRepository;
    private LogIntelligenceService service;

    @BeforeEach
    void setUp() {
        logRepository = Mockito.mock(LogEntryRepository.class);
        LogClusterRepository clusterRepository = Mockito.mock(LogClusterRepository.class);
        ExceptionFingerprintRepository fingerprintRepository = Mockito.mock(ExceptionFingerprintRepository.class);
        LogAnalysisResultRepository analysisRepository = Mockito.mock(LogAnalysisResultRepository.class);

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
    void testUnauthorizedLogAccessThrowsNotFound() {
        UUID logId = UUID.randomUUID();
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.analyzeLog(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), logId);
        });
    }
}
