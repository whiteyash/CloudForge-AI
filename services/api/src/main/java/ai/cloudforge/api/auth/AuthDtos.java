package ai.cloudforge.api.auth;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 12, max = 128) String password,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Size(max = 120) String organizationName) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record UserSummary(UUID id, String email, String fullName) {
    }

    public record OrganizationSummary(UUID id, String name, String slug, String role) {
    }

    public record AuthResponse(
            String accessToken,
            long expiresInSeconds,
            UserSummary user,
            List<OrganizationSummary> organizations) {
    }
}
