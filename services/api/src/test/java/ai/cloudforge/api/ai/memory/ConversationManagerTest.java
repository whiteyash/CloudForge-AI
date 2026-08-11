package ai.cloudforge.api.ai.memory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationManagerTest {

    private ConversationManager manager;

    @BeforeEach
    void setUp() {
        manager = new ConversationManager();
    }

    @Test
    void testCreateAndCloseConversation() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ConversationManager.ConversationSession session = manager.createConversation(projectId, userId);

        assertNotNull(session);
        assertEquals("ACTIVE", session.status());

        manager.closeConversation(session.id());
        ConversationManager.ConversationSession closed = manager.getConversation(session.id());
        assertEquals("CLOSED", closed.status());
    }
}
