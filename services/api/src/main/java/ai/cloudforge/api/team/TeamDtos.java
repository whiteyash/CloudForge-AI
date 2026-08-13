package ai.cloudforge.api.team;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TeamDtos {

    public record CreateTeamRequest(
            @NotBlank(message = "Team name is required")
            @Size(max = 120, message = "Team name must be 120 characters or fewer")
            String name,
            String description
    ) {}

    public record UpdateTeamRequest(
            @Size(max = 120, message = "Team name must be 120 characters or fewer")
            String name,
            String description
    ) {}

    public record AddTeamMemberRequest(
            UUID userId,
            String role
    ) {}

    public record UpdateTeamMemberRoleRequest(
            String role
    ) {}

    public record TeamMemberSummary(
            UUID userId,
            String email,
            String fullName,
            String role,
            Instant addedAt
    ) {}

    public record TeamResponse(
            UUID id,
            UUID orgId,
            String name,
            String slug,
            String description,
            String status,
            int membersCount,
            List<TeamMemberSummary> members,
            Instant createdAt
    ) {}
}
