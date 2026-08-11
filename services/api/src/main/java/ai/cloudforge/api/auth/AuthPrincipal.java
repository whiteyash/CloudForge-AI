package ai.cloudforge.api.auth;

import java.util.UUID;

public record AuthPrincipal(UUID userId, String email) {
}
