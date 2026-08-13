package ai.cloudforge.api.team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ai.cloudforge.api.auth.AppUser;
import ai.cloudforge.api.auth.AppUserRepository;
import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.MembershipRepository;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.team.TeamDtos.CreateTeamRequest;
import ai.cloudforge.api.team.TeamDtos.TeamResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMembershipRepository teamMembershipRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private RbacService rbacService;
    @Mock
    private AuditLogRepository auditLogRepository;

    private TeamService teamService;

    private UUID userId;
    private UUID orgId;
    private Organization org;
    private AppUser user;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(
                teamRepository,
                teamMembershipRepository,
                organizationRepository,
                membershipRepository,
                userRepository,
                rbacService,
                auditLogRepository
        );

        userId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        org = new Organization("CloudForge System", "cloudforge-system");
        user = new AppUser("admin@cloudforge.ai", "hash", "Platform Engineer");
    }

    @Test
    @DisplayName("createTeam succeeds with real DB persistence and audit log")
    void createTeam_succeeds() {
        CreateTeamRequest request = new CreateTeamRequest("Platform Infrastructure", "K8s and Terraform");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(teamRepository.existsByOrganizationIdAndEnvironmentAndNameIgnoreCase(orgId, "DEV", "Platform Infrastructure")).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Team savedTeam = new Team(org, "Platform Infrastructure", "K8s and Terraform", userId, "DEV");
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        TeamResponse response = teamService.createTeam(userId, orgId, request, "DEV");

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Platform Infrastructure");
        verify(rbacService).requirePermission(userId, orgId, "team.create");
        verify(teamRepository).save(any(Team.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("createTeam throws exception on duplicate name in same environment")
    void createTeam_duplicateName_throws() {
        CreateTeamRequest request = new CreateTeamRequest("Platform Infrastructure", "K8s");

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(teamRepository.existsByOrganizationIdAndEnvironmentAndNameIgnoreCase(orgId, "DEV", "Platform Infrastructure")).thenReturn(true);

        assertThatThrownBy(() -> teamService.createTeam(userId, orgId, request, "DEV"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A team with this name already exists in this environment");
    }

    @Test
    @DisplayName("getTeamsForOrg returns environment scoped teams")
    void getTeamsForOrg_environmentScoped() {
        Team devTeam = new Team(org, "Dev Team", "Desc", userId, "DEV");
        when(teamRepository.findByOrganizationIdAndEnvironment(orgId, "DEV")).thenReturn(List.of(devTeam));

        List<TeamResponse> result = teamService.getTeamsForOrg(userId, orgId, null, "DEV");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Dev Team");
        verify(rbacService).requirePermission(userId, orgId, "team.list");
    }

    @Test
    @DisplayName("addMember succeeds when user is org member")
    void addMember_succeeds() {
        UUID memberUserId = UUID.randomUUID();
        AppUser memberUser = new AppUser("dev@cloudforge.ai", "hash", "Dev User");
        Team team = new Team(org, "Core", "Desc", userId, "DEV");

        when(teamRepository.findByIdAndOrganizationId(team.getId(), orgId)).thenReturn(Optional.of(team));
        when(userRepository.findById(memberUserId)).thenReturn(Optional.of(memberUser));
        when(membershipRepository.existsByOrganizationIdAndUserId(orgId, memberUserId)).thenReturn(true);
        when(teamMembershipRepository.findByTeamIdAndUserId(team.getId(), memberUserId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        TeamResponse response = teamService.addMember(userId, orgId, team.getId(), memberUserId, "MEMBER", "DEV");

        assertThat(response).isNotNull();
        verify(rbacService).requirePermission(userId, orgId, "team.member.add");
        verify(teamMembershipRepository).save(any(TeamMembership.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
