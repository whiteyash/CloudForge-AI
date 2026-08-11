package ai.cloudforge.api.aiops;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncidentDtos {

    public record TriggerIncidentRequest(
            @NotBlank String title,
            String description,
            String severity,
            String incidentSource
    ) {}

    public record IncidentResponseDto(
            UUID id,
            UUID projectId,
            UUID organizationId,
            String title,
            String description,
            String severity,
            String status,
            UUID assigneeUserId,
            String incidentSource,
            Instant triggeredAt,
            Instant acknowledgedAt,
            Instant resolvedAt
    ) {}

    public record AcknowledgeIncidentRequest(
            UUID assigneeUserId
    ) {}

    public record ResolveIncidentRequest(
            String resolutionNotes
    ) {}

    public record CreateOnCallScheduleRequest(
            @NotBlank String name,
            String timeZone,
            String rotationType,
            @NotNull UUID activeUserId
    ) {}

    public record OnCallScheduleDto(
            UUID id,
            UUID organizationId,
            String name,
            String timeZone,
            String rotationType,
            UUID activeUserId,
            Instant createdAt
    ) {}

    public record CreateAlertChannelRequest(
            @NotBlank String channelName,
            @NotBlank String channelType,
            String webhookUrl,
            String apiKey
    ) {}

    public record AlertChannelDto(
            UUID id,
            UUID organizationId,
            String channelName,
            String channelType,
            String webhookUrl,
            boolean enabled,
            Instant createdAt
    ) {}

    public record IncidentSummaryDto(
            int totalIncidents,
            int openIncidents,
            int sev1Count,
            int sev2Count,
            int sev3Count,
            int sev4Count,
            double mttmAcknowledgedMinutes,
            double mttrResolvedMinutes,
            List<IncidentResponseDto> recentIncidents,
            List<OnCallScheduleDto> onCallSchedules
    ) {}
}
