package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.ai.core.AuditLogger;
import ai.cloudforge.api.ai.core.ContextBuilder;
import ai.cloudforge.api.ai.core.IntentResolver;
import ai.cloudforge.api.ai.core.LLMProvider;
import ai.cloudforge.api.ai.core.MockLLMProvider;
import ai.cloudforge.api.ai.memory.AIConversationResponse;
import ai.cloudforge.api.ai.memory.ConversationManager;

class CopilotServiceTest {

    private CopilotSessionRepository sessionRepository;
    private CopilotMessageRepository messageRepository;
    private IntentHistoryRepository intentHistoryRepository;
    private CopilotService service;

    @BeforeEach
    void setUp() {
        sessionRepository = Mockito.mock(CopilotSessionRepository.class);
        messageRepository = Mockito.mock(CopilotMessageRepository.class);
        intentHistoryRepository = Mockito.mock(IntentHistoryRepository.class);

        IntentResolver intentResolver = new IntentResolver();
        ContextBuilder contextBuilder = new ContextBuilder();
        LLMProvider llmProvider = new MockLLMProvider();
        AuditLogger auditLogger = new AuditLogger();
        ConversationManager conversationManager = new ConversationManager();

        IntentRouter intentRouter = new IntentRouter(intentResolver);
        ContextAggregationService contextAggregationService = new ContextAggregationService(contextBuilder);
        ConversationOrchestrator conversationOrchestrator = new ConversationOrchestrator(conversationManager);

        service = new CopilotService(
                sessionRepository, messageRepository, intentHistoryRepository,
                intentRouter, contextAggregationService, conversationOrchestrator,
                llmProvider, auditLogger, null
        );
    }

    @Test
    void testProcessCopilotChatReturnsAIConversationResponse() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(sessionRepository.save(any(CopilotSession.class))).thenAnswer(inv -> inv.getArgument(0));

        AIConversationResponse<CopilotService.CopilotResponse> response = service.processCopilotChat(orgId, userId, projectId, null, "Why did my deployment fail?");

        assertNotNull(response);
        assertEquals(95, response.baseResponse().confidence());
        assertNotNull(response.baseResponse().payload());
    }
}
