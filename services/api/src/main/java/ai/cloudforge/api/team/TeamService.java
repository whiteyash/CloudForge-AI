package ai.cloudforge.api.team;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AppUser;
import ai.cloudforge.api.auth.AppUserRepository;
import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.team.TeamDtos.CreateTeamRequest;
import ai.cloudforge.api.team.TeamDtos.TeamMemberSummary;
import ai.cloudforge.api.team.TeamDtos.TeamResponse;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final OrganizationRepository organizationRepository;
    private final AppUserRepository userRepository;
    private final RbacService rbacService;
    private final AuditLogRepository auditLogRepository;

    public TeamService(
            TeamRepository teamRepository,
            TeamMembershipRepository teamMembershipRepository,
            OrganizationRepository organizationRepository,
            AppUserRepository userRepository,
            RbacService rbacService,
            AuditLogRepository auditLogRepository) {
        this.teamRepository = teamRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.rbacService = rbacService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsForOrg(UUID userId, UUID orgId) {
        rbacService.getRole(userId, orgId);
        return teamRepository.findByOrganizationId(orgId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID userId, UUID orgId, UUID teamId) {
        rbacService.getRole(userId, orgId);
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return toResponse(team);
    }

    @Transactional
    public TeamResponse createTeam(UUID userId, UUID orgId, CreateTeamRequest request) {
        rbacService.requireAdminOrOwner(userId, orgId);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (teamRepository.existsByOrganizationIdAndNameIgnoreCase(orgId, request.name().trim())) {
            throw new IllegalArgumentException("A team with this name already exists in the organization");
        }

        Team team = teamRepository.save(new Team(org, request.name().trim(), request.description()));
        auditLogRepository.save(new AuditLog(orgId, userId, "team.created", team.getName()));

        return toResponse(team);
    }

    @Transactional
    public TeamResponse addMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToAdd) {
        rbacService.requireAdminOrOwner(requesterId, orgId);

        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        AppUser user = userRepository.findById(userIdToAdd)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (teamMembershipRepository.findByTeamIdAndUserId(teamId, userIdToAdd).isEmpty()) {
            teamMembershipRepository.save(new TeamMembership(team, user));
            auditLogRepository.save(new AuditLog(orgId, requesterId, "team.member_added", team.getName() + ":" + user.getEmail()));
        }

        return toResponse(team);
    }

    @Transactional
    public TeamResponse removeMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToRemove) {
        rbacService.requireAdminOrOwner(requesterId, orgId);

        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        teamMembershipRepository.deleteByTeamIdAndUserId(teamId, userIdToRemove);
        auditLogRepository.save(new AuditLog(orgId, requesterId, "team.member_removed", team.getName() + ":" + userIdToRemove));

        return toResponse(team);
    }

    @Transactional
    public void deleteTeam(UUID requesterId, UUID orgId, UUID teamId) {
        rbacService.requireAdminOrOwner(requesterId, orgId);
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamRepository.delete(team);
        auditLogRepository.save(new AuditLog(orgId, requesterId, "team.deleted", team.getName()));
    }

    private TeamResponse toResponse(Team team) {
        List<TeamMemberSummary> members = teamMembershipRepository.findByTeamId(team.getId()).stream()
                .map(m -> new TeamMemberSummary(
                        m.getUser().getId(),
                        m.getUser().getEmail(),
                        m.getUser().getFullName(),
                        m.getCreatedAt()
                ))
                .toList();

        return new TeamResponse(
                team.getId(),
                team.getOrganization().getId(),
                team.getName(),
                team.getDescription(),
                members,
                team.getCreatedAt()
        );
    }
}
