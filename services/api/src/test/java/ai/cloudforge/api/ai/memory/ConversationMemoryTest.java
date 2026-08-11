package ai.cloudforge.api.ai.memory;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationMemoryTest {

    private ConversationMemory memory;

    @BeforeEach
    void setUp() {
        memory = new ConversationMemory();
    }

    @Test
    void testShortTermAndLongTermMemory() {
        UUID conversationId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        memory.addShortTermMessage(conversationId, "Message 1");
        List<String> history = memory.getShortTermHistory(conversationId);
        assertEquals(1, history.size());

        memory.pinLongTermItem(projectId, "Pinned Incident #102");
        List<String> pinned = memory.getPinnedLongTermItems(projectId);
        assertEquals(1, pinned.size());
    }
}
