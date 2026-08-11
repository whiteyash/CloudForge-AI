package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ai.cloudforge.api.ai.memory.ConversationManager;

class ConversationOrchestratorTest {

    private ConversationOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        ConversationManager manager = new ConversationManager();
        orchestrator = new ConversationOrchestrator(manager);
    }

    @Test
    void testOrchestrateSession() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ConversationManager.ConversationSession session = orchestrator.orchestrateSession(projectId, userId, null);
        assertNotNull(session);
        assertNotNull(session.id());
    }
}
