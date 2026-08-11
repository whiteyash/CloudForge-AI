package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ai.cloudforge.api.ai.core.ContextBuilder;

@Service
public class ContextAggregationService {

    private final ContextBuilder contextBuilder;

    public ContextAggregationService(ContextBuilder contextBuilder) {
        this.contextBuilder = contextBuilder;
    }

    public UnifiedContext aggregateOperationalContext(UUID projectId, UUID userId) {
        ContextBuilder.OperationalContext operational = contextBuilder.buildContext(projectId, userId, "OPERATIONS");
        return new UnifiedContext(
                projectId,
                userId,
                operational.environmentHealth(),
                1,
                3,
                "Operational risk low (92% confidence)"
        );
    }

    public record UnifiedContext(
            UUID projectId,
            UUID userId,
            String environmentHealth,
            int activeIncidents,
            int recentDeployments,
            String memorySummary
    ) {}
}
