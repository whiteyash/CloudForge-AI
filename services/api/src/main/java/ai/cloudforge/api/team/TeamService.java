package ai.cloudforge.api.team;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AppUser;
import ai.cloudforge.api.auth.AppUserRepository;
import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.MembershipRepository;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.team.TeamDtos.CreateTeamRequest;
import ai.cloudforge.api.team.TeamDtos.TeamMemberSummary;
import ai.cloudforge.api.team.TeamDtos.TeamResponse;
import ai.cloudforge.api.team.TeamDtos.UpdateTeamRequest;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final AppUserRepository userRepository;
    private final RbacService rbacService;
    private final AuditLogRepository auditLogRepository;

    public TeamService(
            TeamRepository teamRepository,
            TeamMembershipRepository teamMembershipRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            AppUserRepository userRepository,
            RbacService rbacService,
            AuditLogRepository auditLogRepository) {
        this.teamRepository = teamRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.rbacService = rbacService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsForOrg(UUID userId, UUID orgId) {
        return getTeamsForOrg(userId, orgId, null, "DEV");
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsForOrg(UUID userId, UUID orgId, String search) {
        return getTeamsForOrg(userId, orgId, search, "DEV");
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsForOrg(UUID userId, UUID orgId, String search, String environment) {
        rbacService.requirePermission(userId, orgId, "team.list");
        String envFilter = (environment != null && !environment.isBlank()) ? environment.toUpperCase() : "DEV";
        List<Team> teams = teamRepository.findByOrganizationIdAndEnvironment(orgId, envFilter);

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            teams = teams.stream()
                    .filter(t -> (t.getName() != null && t.getName().toLowerCase().contains(q)) ||
                                 (t.getSlug() != null && t.getSlug().toLowerCase().contains(q)) ||
                                 (t.getDescription() != null && t.getDescription().toLowerCase().contains(q)))
                    .toList();
        }

        return teams.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID userId, UUID orgId, UUID teamId) {
        rbacService.requirePermission(userId, orgId, "team.view");
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));
        return toResponse(team);
    }

    @Transactional
    public TeamResponse createTeam(UUID userId, UUID orgId, CreateTeamRequest request) {
        return createTeam(userId, orgId, request, "DEV");
    }

    @Transactional
    public TeamResponse createTeam(UUID userId, UUID orgId, CreateTeamRequest request, String environment) {
        rbacService.requirePermission(userId, orgId, "team.create");

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        String envTarget = (environment != null && !environment.isBlank()) ? environment.toUpperCase() : "DEV";

        if (teamRepository.existsByOrganizationIdAndEnvironmentAndNameIgnoreCase(orgId, envTarget, request.name().trim())) {
            throw new IllegalArgumentException("A team with this name already exists in this environment");
        }

        Team team = teamRepository.save(new Team(org, request.name().trim(), request.description(), userId, envTarget));
        
        // Auto-add creator as Team Lead/Admin
        AppUser creatorUser = userRepository.findById(userId).orElse(null);
        if (creatorUser != null) {
            teamMembershipRepository.save(new TeamMembership(team, creatorUser, "LEAD"));
        }

        String actorEmail = creatorUser != null ? creatorUser.getEmail() : null;
        auditLogRepository.save(new AuditLog(orgId, userId, "team.created", team.getName(), envTarget, actorEmail, null));

        return toResponse(team);
    }

    @Transactional
    public TeamResponse updateTeam(UUID userId, UUID orgId, UUID teamId, UpdateTeamRequest request) {
        return updateTeam(userId, orgId, teamId, request, "DEV");
    }

    @Transactional
    public TeamResponse updateTeam(UUID userId, UUID orgId, UUID teamId, UpdateTeamRequest request, String environment) {
        rbacService.requirePermission(userId, orgId, "team.update");
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));

        if (request.name() != null && !request.name().isBlank()) {
            String trimmedName = request.name().trim();
            if (!trimmedName.equalsIgnoreCase(team.getName()) &&
                    teamRepository.existsByOrganizationIdAndNameIgnoreCase(orgId, trimmedName)) {
                throw new IllegalArgumentException("A team with this name already exists in the organization");
            }
            team.setName(trimmedName);
            team.setSlug(trimmedName.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", ""));
        }

        if (request.description() != null) {
            team.setDescription(request.description());
        }

        Team updated = teamRepository.save(team);
        String actorEmail = userRepository.findById(userId).map(AppUser::getEmail).orElse(null);
        auditLogRepository.save(new AuditLog(orgId, userId, "team.updated", updated.getName(), environment, actorEmail, null));
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberSummary> getTeamMembers(UUID userId, UUID orgId, UUID teamId) {
        rbacService.requirePermission(userId, orgId, "team.view");
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));
        return getTeamMemberSummaries(team.getId());
    }

    @Transactional
    public TeamResponse addMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToAdd) {
        return addMember(requesterId, orgId, teamId, userIdToAdd, "MEMBER", "DEV");
    }

    @Transactional
    public TeamResponse addMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToAdd, String role) {
        return addMember(requesterId, orgId, teamId, userIdToAdd, role, "DEV");
    }

    @Transactional
    public TeamResponse addMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToAdd, String role, String environment) {
        rbacService.requirePermission(requesterId, orgId, "team.member.add");

        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));

        AppUser user = userRepository.findById(userIdToAdd)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!membershipRepository.existsByOrganizationIdAndUserId(orgId, userIdToAdd)) {
            throw new IllegalArgumentException("User must be a member of the organization before joining a team");
        }

        Optional<TeamMembership> existing = teamMembershipRepository.findByTeamIdAndUserId(teamId, userIdToAdd);
        if (existing.isEmpty()) {
            teamMembershipRepository.save(new TeamMembership(team, user, role != null ? role : "MEMBER"));
            String actorEmail = userRepository.findById(requesterId).map(AppUser::getEmail).orElse(null);
            auditLogRepository.save(new AuditLog(orgId, requesterId, "team.member_added", team.getName() + ":" + user.getEmail(), environment, actorEmail, null));
        }

        return toResponse(team);
    }

    @Transactional
    public TeamResponse updateMemberRole(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToUpdate, String newRole) {
        return updateMemberRole(requesterId, orgId, teamId, userIdToUpdate, newRole, "DEV");
    }

    @Transactional
    public TeamResponse updateMemberRole(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToUpdate, String newRole, String environment) {
        rbacService.requirePermission(requesterId, orgId, "team.member.role.change");

        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));

        TeamMembership membership = teamMembershipRepository.findByTeamIdAndUserId(teamId, userIdToUpdate)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in team"));

        membership.setRole(newRole != null ? newRole : "MEMBER");
        teamMembershipRepository.save(membership);

        String actorEmail = userRepository.findById(requesterId).map(AppUser::getEmail).orElse(null);
        auditLogRepository.save(new AuditLog(orgId, requesterId, "team.member_role_updated", team.getName() + ":" + userIdToUpdate + ":" + newRole, environment, actorEmail, null));

        return toResponse(team);
    }

    @Transactional
    public TeamResponse removeMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToRemove) {
        return removeMember(requesterId, orgId, teamId, userIdToRemove, "DEV");
    }

    @Transactional
    public TeamResponse removeMember(UUID requesterId, UUID orgId, UUID teamId, UUID userIdToRemove, String environment) {
        rbacService.requirePermission(requesterId, orgId, "team.member.remove");

        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));

        teamMembershipRepository.deleteByTeamIdAndUserId(teamId, userIdToRemove);
        String actorEmail = userRepository.findById(requesterId).map(AppUser::getEmail).orElse(null);
        auditLogRepository.save(new AuditLog(orgId, requesterId, "team.member_removed", team.getName() + ":" + userIdToRemove, environment, actorEmail, null));

        return toResponse(team);
    }

    @Transactional
    public void deleteTeam(UUID requesterId, UUID orgId, UUID teamId) {
        deleteTeam(requesterId, orgId, teamId, "DEV");
    }

    @Transactional
    public void deleteTeam(UUID requesterId, UUID orgId, UUID teamId, String environment) {
        rbacService.requirePermission(requesterId, orgId, "team.delete");
        Team team = teamRepository.findByIdAndOrganizationId(teamId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found in this organization"));
        
        teamMembershipRepository.deleteByTeamId(teamId);
        teamRepository.delete(team);

        String actorEmail = userRepository.findById(requesterId).map(AppUser::getEmail).orElse(null);
        auditLogRepository.save(new AuditLog(orgId, requesterId, "team.deleted", team.getName(), environment, actorEmail, null));
    }

    private List<TeamMemberSummary> getTeamMemberSummaries(UUID teamId) {
        return teamMembershipRepository.findByTeamId(teamId).stream()
                .map(m -> new TeamMemberSummary(
                        m.getUser().getId(),
                        m.getUser().getEmail(),
                        m.getUser().getFullName(),
                        m.getRole() != null ? m.getRole() : "MEMBER",
                        m.getCreatedAt()
                ))
                .toList();
    }

    private TeamResponse toResponse(Team team) {
        List<TeamMemberSummary> members = getTeamMemberSummaries(team.getId());
        return new TeamResponse(
                team.getId(),
                team.getOrganization().getId(),
                team.getName(),
                team.getSlug() != null ? team.getSlug() : team.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-"),
                team.getDescription(),
                team.getStatus() != null ? team.getStatus() : "ACTIVE",
                members.size(),
                members,
                team.getCreatedAt()
        );
    }
}
