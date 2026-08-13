package ai.cloudforge.api.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityCenterService {

    private final AppUserRepository userRepository;
    private final ActiveSessionRepository activeSessionRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final FavoriteWorkspaceRepository favoriteWorkspaceRepository;
    private final ai.cloudforge.api.aiops.IncidentRepository incidentRepository;

    public SecurityCenterService(
            AppUserRepository userRepository,
            ActiveSessionRepository activeSessionRepository,
            LoginAttemptRepository loginAttemptRepository,
            FavoriteWorkspaceRepository favoriteWorkspaceRepository,
            ai.cloudforge.api.aiops.IncidentRepository incidentRepository) {
        this.userRepository = userRepository;
        this.activeSessionRepository = activeSessionRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.favoriteWorkspaceRepository = favoriteWorkspaceRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional(readOnly = true)
    public SecurityOverviewResponse getSecurityOverview(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long activeSessionsCount = activeSessionRepository.findByUserIdOrderByLastActiveAtDesc(userId).size();
        long passwordAgeDays = Duration.between(user.getCreatedAt(), Instant.now()).toDays();
        long recentFailedAttempts = loginAttemptRepository.countByEmailAndSuccessFalse(user.getEmail());
        long openIncidentsCount = incidentRepository.findAll().stream()
                .filter(inc -> !"RESOLVED".equalsIgnoreCase(inc.getStatus()) && !"CLOSED".equalsIgnoreCase(inc.getStatus()))
                .count();

        int securityScore = 100;
        if (passwordAgeDays >= 90) securityScore -= 10;
        if (recentFailedAttempts > 0) securityScore -= 10;
        if (openIncidentsCount > 0) securityScore -= (int) (openIncidentsCount * 15);
        securityScore = Math.max(0, Math.min(100, securityScore));

        return new SecurityOverviewResponse(
                user.getId(),
                user.getEmail(),
                securityScore,
                passwordAgeDays,
                activeSessionsCount,
                recentFailedAttempts,
                false, // MFA ready architecture
                List.of(
                        "Rotate API keys every 90 days",
                        "Audit active sessions quarterly",
                        "Review granted organization permissions"
                )
        );
    }

    @Transactional
    public void addFavoriteWorkspace(UUID userId, UUID orgId) {
        if (favoriteWorkspaceRepository.findByUserIdAndOrgId(userId, orgId).isEmpty()) {
            favoriteWorkspaceRepository.save(new FavoriteWorkspace(userId, orgId, true));
        }
    }

    @Transactional
    public void removeFavoriteWorkspace(UUID userId, UUID orgId) {
        favoriteWorkspaceRepository.findByUserIdAndOrgId(userId, orgId)
                .ifPresent(favoriteWorkspaceRepository::delete);
    }

    public record SecurityOverviewResponse(
            UUID userId,
            String email,
            int securityHealthScore,
            long passwordAgeDays,
            long activeSessionsCount,
            long recentFailedAttemptsCount,
            boolean mfaEnabled,
            List<String> securityRecommendations
    ) {}
}
