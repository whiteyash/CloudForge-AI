package ai.cloudforge.api.aiops;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AIChatServiceTest {

    private AiOperationHistoryRepository historyRepository;
    private AIChatService chatService;

    @BeforeEach
    void setUp() {
        historyRepository = Mockito.mock(AiOperationHistoryRepository.class);
        chatService = new AIChatService(historyRepository);
    }

    @Test
    void testProcessQueryRespondsToFailurePrompt() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        AIChatService.ChatResponse response = chatService.processQuery(projectId, userId, "Why did the deployment fail?");

        assertNotNull(response);
        assertEquals("Why did the deployment fail?", response.prompt());
        assertTrue(response.response().contains("runner agent disconnect"));
    }
}
