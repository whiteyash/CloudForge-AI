package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.cloudforge.api.ai.memory.ConversationManager;

@Service
public class ConversationOrchestrator {

    private final ConversationManager conversationManager;

    public ConversationOrchestrator(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    public ConversationManager.ConversationSession orchestrateSession(UUID projectId, UUID userId, UUID conversationId) {
        if (conversationId == null) {
            return conversationManager.createConversation(projectId, userId);
        }
        return conversationManager.getConversation(conversationId);
    }
}
