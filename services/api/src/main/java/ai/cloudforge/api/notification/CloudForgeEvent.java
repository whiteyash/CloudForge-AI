package ai.cloudforge.api.notification;

import java.time.Instant;
import java.util.UUID;

public record CloudForgeEvent(
        UUID correlationId,
        UUID orgId,
        UUID userId,
        String action,
        String target,
        String category,
        String severity,
        String message,
        Instant timestamp
) {
    public CloudForgeEvent(UUID orgId, UUID userId, String action, String target, String message) {
        this(UUID.randomUUID(), orgId, userId, action, target, "SYSTEM", "INFO", message, Instant.now());
    }
}
