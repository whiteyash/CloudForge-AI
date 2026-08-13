package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import ai.cloudforge.api.aiops.Incident;
import ai.cloudforge.api.aiops.IncidentRepository;
import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.MembershipRepository;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@Service
public class ContextAggregationService {

    private final ProjectRepository projectRepository;
    private final IncidentRepository incidentRepository;
    private final AuditLogRepository auditLogRepository;
    private final MembershipRepository membershipRepository;

    public ContextAggregationService(
            @Nullable ProjectRepository projectRepository,
            @Nullable IncidentRepository incidentRepository,
            @Nullable AuditLogRepository auditLogRepository,
            @Nullable MembershipRepository membershipRepository) {
        this.projectRepository = projectRepository;
        this.incidentRepository = incidentRepository;
        this.auditLogRepository = auditLogRepository;
        this.membershipRepository = membershipRepository;
    }

    public UnifiedContext aggregateOperationalContext(UUID orgId, UUID projectId, UUID userId, String environment) {
        String env = (environment != null && !environment.isBlank()) ? environment.toUpperCase() : "DEV";

        long projectsCount = 0;
        String projectName = "General Workspace";
        if (projectRepository != null) {
            if (orgId != null) {
                projectsCount = projectRepository.findByOrganizationId(orgId).size();
            }
            if (projectId != null) {
                projectName = projectRepository.findById(projectId).map(Project::getName).orElse("Project #" + projectId);
            }
        }

        int activeIncidentsCount = 0;
        String incidentSummary = "No active incidents reported.";
        if (incidentRepository != null && projectId != null) {
            List<Incident> incidents = incidentRepository.findByProjectId(projectId).stream()
                    .filter(i -> !"RESOLVED".equalsIgnoreCase(i.getStatus()))
                    .toList();
            activeIncidentsCount = incidents.size();
            if (!incidents.isEmpty()) {
                incidentSummary = activeIncidentsCount + " active incident(s): " +
                        incidents.stream().map(i -> i.getTitle() + " [" + i.getSeverity() + "]").reduce((a, b) -> a + "; " + b).orElse("");
            }
        }

        long membersCount = 0;
        if (membershipRepository != null && orgId != null) {
            membersCount = membershipRepository.findByOrganizationId(orgId).size();
        }

        String recentAuditLogsSummary = "No recent audit events.";
        if (auditLogRepository != null && orgId != null) {
            List<AuditLog> logs = auditLogRepository.findByOrganizationIdAndEnvironmentOrderByCreatedAtDesc(orgId, env);
            if (!logs.isEmpty()) {
                recentAuditLogsSummary = logs.stream().limit(5)
                        .map(l -> l.getAction() + " on " + l.getTarget())
                        .reduce((a, b) -> a + " | " + b)
                        .orElse("None");
            }
        }

        String envHealth = activeIncidentsCount == 0 ? "HEALTHY" : (activeIncidentsCount > 2 ? "CRITICAL" : "DEGRADED");

        return new UnifiedContext(
                orgId,
                projectId,
                userId,
                env,
                projectName,
                envHealth,
                activeIncidentsCount,
                incidentSummary,
                projectsCount,
                membersCount,
                recentAuditLogsSummary
        );
    }

    public record UnifiedContext(
            UUID orgId,
            UUID projectId,
            UUID userId,
            String environment,
            String projectName,
            String environmentHealth,
            int activeIncidents,
            String incidentSummary,
            long projectsCount,
            long membersCount,
            String recentAuditLogsSummary
    ) {}
}
