package ai.cloudforge.api.security;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class SecurityScanDtos {

    public record TriggerScanRequest(
            @NotBlank String targetType,
            @NotBlank String targetName,
            String scannerEngine
    ) {}

    public record ScanResponseDto(
            UUID id,
            UUID projectId,
            UUID organizationId,
            String targetType,
            String targetName,
            String scanStatus,
            String scannerEngine,
            int totalVulnerabilities,
            int criticalCount,
            int highCount,
            int mediumCount,
            int lowCount,
            Instant scannedAt
    ) {}

    public record FindingResponseDto(
            UUID id,
            UUID scanId,
            String cveId,
            String packageName,
            String installedVersion,
            String fixedVersion,
            String severity,
            String title,
            String description,
            String remediationSuggestion,
            String status
    ) {}

    public record UpdateFindingStatusRequest(
            @NotBlank String status
    ) {}

    public record GenerateSbomRequest(
            @NotBlank String targetName,
            String format
    ) {}

    public record SbomResponseDto(
            UUID id,
            UUID projectId,
            String targetName,
            String format,
            String specVersion,
            int totalComponents,
            String documentJson,
            Instant generatedAt
    ) {}

    public record SecuritySummaryDto(
            int totalScans,
            int openVulnerabilities,
            int criticalCount,
            int highCount,
            int mediumCount,
            int lowCount,
            int sbomCount,
            String lastScannedAt,
            List<ScanResponseDto> recentScans
    ) {}
}
