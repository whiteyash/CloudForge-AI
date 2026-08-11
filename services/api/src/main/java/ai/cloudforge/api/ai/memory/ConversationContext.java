package ai.cloudforge.api.ai.memory;

import java.util.List;
import java.util.UUID;

public record ConversationContext(
        UUID projectId,
        UUID organizationId,
        UUID userId,
        String repositoryName,
        String pipelineName,
        String deploymentTarget,
        String environmentName,
        String incidentTitle,
        List<String> historyMessages
) {
    public ConversationContext {
        historyMessages = historyMessages != null ? List.copyOf(historyMessages) : List.of();
    }
}
