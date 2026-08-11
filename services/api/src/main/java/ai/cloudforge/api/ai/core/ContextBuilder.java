package ai.cloudforge.api.ai.core;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ContextBuilder {

    public OperationalContext buildContext(UUID projectId, UUID userId, String targetEntity) {
        return new OperationalContext(
                projectId,
                userId,
                targetEntity != null ? targetEntity : "PROJECT",
                "HEALTHY",
                "STAGING-US-EAST",
                87.5
        );
    }

    public record OperationalContext(
            UUID projectId,
            UUID userId,
            String targetEntity,
            String environmentHealth,
            String activeTarget,
            double runnerCapacityPercent
    ) {}
}
