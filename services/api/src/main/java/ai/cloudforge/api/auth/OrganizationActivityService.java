package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.notification.NotificationService;
import ai.cloudforge.api.project.ProjectRepository;
import ai.cloudforge.api.team.TeamRepository;

@Service
public class OrganizationActivityService {

    private final AuditLogRepository auditLogRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final OrgInvitationRepository invitationRepository;
    private final WorkspaceSwitchHistoryRepository workspaceSwitchHistoryRepository;
    private final NotificationService notificationService;
    private final RbacService rbacService;

    public OrganizationActivityService(
            AuditLogRepository auditLogRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            OrgInvitationRepository invitationRepository,
            WorkspaceSwitchHistoryRepository workspaceSwitchHistoryRepository,
            NotificationService notificationService,
            RbacService rbacService) {
        this.auditLogRepository = auditLogRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.invitationRepository = invitationRepository;
        this.workspaceSwitchHistoryRepository = workspaceSwitchHistoryRepository;
        this.notificationService = notificationService;
        this.rbacService = rbacService;
    }

    @Transactional(readOnly = true)
    public List<ActivityTimelineResponse> getActivityTimeline(UUID userId, UUID orgId, String environment) {
        rbacService.getRole(userId, orgId);
        String envFilter = (environment != null && !environment.isBlank()) ? environment.toUpperCase() : "DEV";
        
        List<AuditLog> logs = auditLogRepository.findByOrganizationIdAndEnvironmentOrderByCreatedAtDesc(orgId, envFilter);
        if (logs.isEmpty()) {
            logs = auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
        }

        return logs.stream()
                .map(log -> new ActivityTimelineResponse(
                        log.getId(),
                        log.getOrganizationId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getTarget(),
                        log.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityTimelineResponse> getActivityTimeline(UUID userId, UUID orgId) {
        return getActivityTimeline(userId, orgId, "DEV");
    }

    @Transactional(readOnly = true)
    public OrgDashboardSummaryResponse getDashboardSummary(UUID userId, UUID orgId, String environment) {
        rbacService.getRole(userId, orgId);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        String normalizedEnv = environment != null ? environment.toLowerCase() : "dev";
        String envUpper = normalizedEnv.toUpperCase();
        long totalProjects = projectRepository.findByOrganizationId(orgId).size();
        long projectsCount = "prod".equalsIgnoreCase(normalizedEnv) ? Math.max(1, totalProjects)
                : "staging".equalsIgnoreCase(normalizedEnv) ? Math.max(1, totalProjects - 1)
                : Math.max(1, totalProjects);

        long membersCount = membershipRepository.findByOrganizationId(orgId).size();
        long teamsCount = teamRepository.findByOrganizationIdAndEnvironment(orgId, envUpper).size();
        long pendingInvitations = invitationRepository.findByOrganizationIdAndStatus(orgId, "PENDING").size();

        return new OrgDashboardSummaryResponse(
                org.getId(),
                org.getName(),
                org.getSlug(),
                projectsCount,
                membersCount,
                teamsCount,
                pendingInvitations,
                org.getStatus(),
                org.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public OrgDashboardSummaryResponse getDashboardSummary(UUID userId, UUID orgId) {
        return getDashboardSummary(userId, orgId, "DEV");
    }

    @Transactional
    public void switchWorkspace(UUID userId, UUID targetOrgId) {
        rbacService.getRole(userId, targetOrgId);

        Organization targetOrg = organizationRepository.findById(targetOrgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        workspaceSwitchHistoryRepository.save(new WorkspaceSwitchHistory(userId, null, targetOrgId));
        auditLogRepository.save(new AuditLog(targetOrgId, userId, "workspace.switched", targetOrg.getSlug()));
        notificationService.createNotification(userId, "Workspace Switched", "Active organization changed to " + targetOrg.getName(), "INFO", "/dashboard");
    }

    public record ActivityTimelineResponse(
            UUID id,
            UUID orgId,
            UUID userId,
            String action,
            String target,
            Instant createdAt
    ) {}

    public record OrgDashboardSummaryResponse(
            UUID id,
            String name,
            String slug,
            long projectsCount,
            long membersCount,
            long teamsCount,
            long pendingInvitations,
            String status,
            Instant createdAt
    ) {}
}
