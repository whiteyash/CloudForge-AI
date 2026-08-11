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

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final UUID DEFAULT_PROJECT_ID = UUID.fromString("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33");

    private final AppUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            AppUserRepository userRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            ProjectRepository projectRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
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

        // 4. Ensure Default Project exists with UUID c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33
        if (!projectRepository.existsById(DEFAULT_PROJECT_ID)) {
            Project defaultProject = new Project(org, "CloudForge Core Platform", "https://github.com/cloudforge/cloudforge-ai", "prod-cloudforge");
            defaultProject.setId(DEFAULT_PROJECT_ID);
            projectRepository.save(defaultProject);
        }
    }
}
