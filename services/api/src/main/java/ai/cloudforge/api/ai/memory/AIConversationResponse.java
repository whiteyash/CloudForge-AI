package ai.cloudforge.api.ai.memory;

import java.util.UUID;

import ai.cloudforge.api.ai.core.AIResponse;

public record AIConversationResponse<T>(
        UUID conversationId,
        UUID messageId,
        UUID parentMessageId,
        String conversationSummary,
        AIResponse<T> baseResponse
) {}
