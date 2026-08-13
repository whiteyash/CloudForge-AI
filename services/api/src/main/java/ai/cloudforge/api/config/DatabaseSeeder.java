package ai.cloudforge.api.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AppUser;
import ai.cloudforge.api.auth.AppUserRepository;
import ai.cloudforge.api.auth.Membership;
import ai.cloudforge.api.auth.MembershipRepository;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;
import ai.cloudforge.api.auth.Role;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;
import ai.cloudforge.api.team.Team;
import ai.cloudforge.api.team.TeamMembership;
import ai.cloudforge.api.team.TeamMembershipRepository;
import ai.cloudforge.api.team.TeamRepository;

import ai.cloudforge.api.auth.Permission;
import ai.cloudforge.api.auth.PermissionRepository;
import ai.cloudforge.api.auth.RolePermission;
import ai.cloudforge.api.auth.RolePermissionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AppUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            AppUserRepository userRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            TeamMembershipRepository teamMembershipRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedPermissionsAndMatrix();

        String encodedPassword = passwordEncoder.encode("password123");

        // 1. Ensure Organization exists
        Organization org = organizationRepository.findBySlug("cloudforge-system")
                .orElseGet(() -> organizationRepository.save(new Organization("CloudForge System", "cloudforge-system")));

        // 2. Ensure Admin user exists
        AppUser adminUser = userRepository.findByEmailIgnoreCase("admin@cloudforge.ai")
                .orElseGet(() -> userRepository.save(new AppUser("admin@cloudforge.ai", encodedPassword, "Platform Engineer")));
        if (!passwordEncoder.matches("password123", adminUser.getHashedPassword())) {
            adminUser.setHashedPassword(encodedPassword);
            userRepository.save(adminUser);
        }

        if (!membershipRepository.existsByOrganizationIdAndUserId(org.getId(), adminUser.getId())) {
            membershipRepository.save(new Membership(org, adminUser, Role.OWNER));
        }

        // 3. Ensure Developer user exists
        AppUser devUser = userRepository.findByEmailIgnoreCase("developer@cloudforge.ai")
                .orElseGet(() -> userRepository.save(new AppUser("developer@cloudforge.ai", encodedPassword, "Lead Developer")));
        if (!passwordEncoder.matches("password123", devUser.getHashedPassword())) {
            devUser.setHashedPassword(encodedPassword);
            userRepository.save(devUser);
        }

        if (!membershipRepository.existsByOrganizationIdAndUserId(org.getId(), devUser.getId())) {
            membershipRepository.save(new Membership(org, devUser, Role.DEVELOPER));
        }

        // 4. Ensure Default Project exists
        if (projectRepository.findByOrganizationId(org.getId()).isEmpty()) {
            Project defaultProject = new Project(org, "CloudForge Core Platform", "https://github.com/cloudforge/cloudforge-ai", "prod-cloudforge");
            projectRepository.save(defaultProject);
        }

        // 5. Ensure Default Seed Teams exist
        if (teamRepository.findByOrganizationId(org.getId()).isEmpty()) {
            Team platformTeam = teamRepository.save(new Team(org, "Platform Core Team", "Engineers responsible for Kubernetes infrastructure & Spring Boot API", adminUser.getId()));
            teamMembershipRepository.save(new TeamMembership(platformTeam, adminUser, "LEAD"));
            teamMembershipRepository.save(new TeamMembership(platformTeam, devUser, "MEMBER"));

            Team devSecOpsTeam = teamRepository.save(new Team(org, "DevSecOps & Reliability", "Security triage, Prometheus monitoring, and incident response", adminUser.getId()));
            teamMembershipRepository.save(new TeamMembership(devSecOpsTeam, devUser, "LEAD"));
        }
    }

    private void seedPermissionsAndMatrix() {
        log.info("Starting RBAC permissions & role matrix seeding check...");
        java.util.Map<String, String[]> catalog = java.util.LinkedHashMap.newLinkedHashMap(6);
        catalog.put("Organization", new String[]{"organization.view", "organization.update", "organization.delete", "organization.archive", "organization.restore", "organization.transfer"});
        catalog.put("Members", new String[]{"member.invite", "member.remove", "member.update", "member.view", "member.role.change"});
        catalog.put("Teams", new String[]{"team.list", "team.view", "team.create", "team.update", "team.delete", "team.assign", "team.member.add", "team.member.remove", "team.member.role.change"});
        catalog.put("Projects", new String[]{"project.create", "project.update", "project.delete"});
        catalog.put("Audit", new String[]{"audit.view", "audit.export"});
        catalog.put("Settings", new String[]{"settings.manage", "subscription.view", "subscription.manage", "session.manage"});

        java.util.List<Permission> newPermissions = new java.util.ArrayList<>();
        catalog.forEach((module, codes) -> {
            for (String code : codes) {
                if (permissionRepository.findByCode(code).isEmpty()) {
                    newPermissions.add(new Permission(code, module, "Permission for " + code));
                }
            }
        });
        if (!newPermissions.isEmpty()) {
            permissionRepository.saveAllAndFlush(newPermissions);
            log.info("Seeded {} new permissions", newPermissions.size());
        }

        // OWNER Permissions
        String[] ownerPerms = {
                "organization.view", "organization.update", "organization.delete", "organization.archive", "organization.restore", "organization.transfer",
                "member.invite", "member.remove", "member.update", "member.view", "member.role.change",
                "team.list", "team.view", "team.create", "team.update", "team.delete", "team.assign", "team.member.add", "team.member.remove", "team.member.role.change",
                "project.create", "project.update", "project.delete", "audit.view", "audit.export", "settings.manage", "subscription.view", "subscription.manage", "session.manage"
        };
        seedRolePermissions(Role.OWNER, ownerPerms);

        // ADMIN Permissions
        String[] adminPerms = {
                "organization.view", "organization.update", "organization.archive",
                "member.invite", "member.remove", "member.view",
                "team.list", "team.view", "team.create", "team.update", "team.delete", "team.assign", "team.member.add", "team.member.remove", "team.member.role.change",
                "project.create", "project.update", "audit.view", "subscription.view", "session.manage"
        };
        seedRolePermissions(Role.ADMIN, adminPerms);

        // LEAD Permissions
        String[] leadPerms = {
                "organization.view", "member.view",
                "team.list", "team.view", "team.create", "team.update", "team.assign", "team.member.add", "team.member.remove",
                "project.create", "project.update", "audit.view", "subscription.view"
        };
        seedRolePermissions(Role.LEAD, leadPerms);

        // DEVELOPER Permissions
        String[] devPerms = {
                "organization.view", "member.view", "team.list", "team.view", "project.create", "project.update", "audit.view", "subscription.view"
        };
        seedRolePermissions(Role.DEVELOPER, devPerms);

        // VIEWER Permissions
        String[] viewerPerms = {
                "organization.view", "member.view", "team.list", "team.view", "audit.view", "subscription.view"
        };
        seedRolePermissions(Role.VIEWER, viewerPerms);

        log.info("Finished RBAC permissions seeding. Total permissions: {}, Total role-permission mappings: {}",
                permissionRepository.count(), rolePermissionRepository.count());
    }

    private void seedRolePermissions(Role role, String[] permissionCodes) {
        java.util.List<RolePermission> toSave = new java.util.ArrayList<>();
        for (String code : permissionCodes) {
            if (!rolePermissionRepository.existsByRoleAndPermissionCode(role, code)) {
                toSave.add(new RolePermission(role, code));
            }
        }
        if (!toSave.isEmpty()) {
            rolePermissionRepository.saveAllAndFlush(toSave);
            log.info("Seeded {} role-permission mappings for {}", toSave.size(), role);
        }
    }
}
