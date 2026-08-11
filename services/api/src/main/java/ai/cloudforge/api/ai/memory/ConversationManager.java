package ai.cloudforge.api.ai.memory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ConversationManager {

    private final Map<UUID, ConversationSession> activeSessions = new ConcurrentHashMap<>();

    public ConversationSession createConversation(UUID projectId, UUID userId) {
        UUID id = UUID.randomUUID();
        ConversationSession session = new ConversationSession(id, projectId, userId, "ACTIVE");
        activeSessions.put(id, session);
        return session;
    }

    public ConversationSession getConversation(UUID conversationId) {
        return activeSessions.get(conversationId);
    }

    public void closeConversation(UUID conversationId) {
        ConversationSession session = activeSessions.get(conversationId);
        if (session != null) {
            activeSessions.put(conversationId, new ConversationSession(session.id(), session.projectId(), session.userId(), "CLOSED"));
        }
    }

    public record ConversationSession(
            UUID id,
            UUID projectId,
            UUID userId,
            String status
    ) {}
}
