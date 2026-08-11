package ai.cloudforge.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AuthDtos.AuthResponse;
import ai.cloudforge.api.auth.AuthDtos.LoginRequest;
import ai.cloudforge.api.auth.AuthDtos.OrganizationSummary;
import ai.cloudforge.api.auth.AuthDtos.RegisterRequest;
import ai.cloudforge.api.auth.AuthDtos.UserSummary;

@Service
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AppUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ActiveSessionRepository activeSessionRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    public AuthService(
            AppUserRepository userRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            RefreshTokenRepository refreshTokenRepository,
            ActiveSessionRepository activeSessionRepository,
            PasswordHistoryRepository passwordHistoryRepository,
            LoginAttemptRepository loginAttemptRepository,
            AuditLogRepository auditLogRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.activeSessionRepository = activeSessionRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthenticationResult register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        AppUser user = userRepository.save(new AppUser(email, encodedPassword, request.fullName().trim()));
        passwordHistoryRepository.save(new PasswordHistory(user, encodedPassword));

        Organization organization = organizationRepository.save(new Organization(
                request.organizationName().trim(), uniqueSlug(request.organizationName())));
        membershipRepository.save(new Membership(organization, user, Role.OWNER));
        auditLogRepository.save(new AuditLog(organization.getId(), user.getId(), "organization.created", organization.getSlug()));

        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getHashedPassword())) {
            loginAttemptRepository.save(new LoginAttempt(email, "127.0.0.1", "Browser", false, "Invalid password"));
            throw new InvalidCredentialsException();
        }

        loginAttemptRepository.save(new LoginAttempt(email, "127.0.0.1", "Browser", true, null));
        auditLogRepository.save(new AuditLog(null, user.getId(), "auth.login", user.getEmail()));
        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult refresh(String rawToken) {
        RefreshToken currentToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash(rawToken))
                .filter(token -> !token.isExpired())
                .orElseThrow(InvalidRefreshTokenException::new);

        String newRefreshToken = generateRefreshToken();
        currentToken.revoke(hash(newRefreshToken));

        RefreshToken newToken = refreshTokenRepository.save(new RefreshToken(
                currentToken.getUser(),
                hash(newRefreshToken),
                Instant.now().plus(jwtProperties.refreshTokenTtl())));

        String accessToken = jwtTokenService.createAccessToken(currentToken.getUser().getId(), currentToken.getUser().getEmail());
        auditLogRepository.save(new AuditLog(null, currentToken.getUser().getId(), "auth.refresh", currentToken.getUser().getEmail()));

        return new AuthenticationResult(responseFor(currentToken.getUser(), accessToken), newRefreshToken);
    }

    @Transactional
    public void logout(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(t -> t.revoke(null));
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenRepository.revokeAllForUser(userId);
        activeSessionRepository.deleteAllForUser(userId);
        auditLogRepository.save(new AuditLog(null, userId, "auth.logout_all", userId.toString()));
    }

    @Transactional(readOnly = true)
    public List<ActiveSessionResponse> getActiveSessions(UUID userId) {
        return activeSessionRepository.findByUserIdOrderByLastActiveAtDesc(userId).stream()
                .map(ActiveSessionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void terminateSession(UUID userId, UUID sessionId) {
        activeSessionRepository.findByIdAndUserId(sessionId, userId).ifPresent(activeSessionRepository::delete);
    }

    @Transactional(readOnly = true)
    public AuthResponse currentUser(UUID userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
        return responseFor(user, jwtTokenService.createAccessToken(user.getId(), user.getEmail()));
    }

    private AuthenticationResult issueTokens(AppUser user) {
        String refreshToken = generateRefreshToken();
        refreshTokenRepository.save(new RefreshToken(
                user,
                hash(refreshToken),
                Instant.now().plus(jwtProperties.refreshTokenTtl())));

        activeSessionRepository.save(new ActiveSession(
                user,
                UUID.randomUUID().toString(),
                "Desktop",
                "Chrome/Vite Dashboard",
                "macOS",
                "127.0.0.1",
                true));

        String accessToken = jwtTokenService.createAccessToken(user.getId(), user.getEmail());
        return new AuthenticationResult(responseFor(user, accessToken), refreshToken);
    }

    private AuthResponse responseFor(AppUser user, String accessToken) {
        List<OrganizationSummary> organizations = membershipRepository.findByUserId(user.getId()).stream()
                .map(membership -> new OrganizationSummary(
                        membership.getOrganization().getId(),
                        membership.getOrganization().getName(),
                        membership.getOrganization().getSlug(),
                        membership.getRole().name().toLowerCase(Locale.ROOT)))
                .toList();
        return new AuthResponse(
                accessToken,
                jwtTokenService.accessTokenTtlSeconds(),
                new UserSummary(user.getId(), user.getEmail(), user.getFullName()),
                organizations);
    }

    private String uniqueSlug(String organizationName) {
        String baseSlug = organizationName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (baseSlug.isBlank()) {
            baseSlug = "organization";
        }
        baseSlug = baseSlug.substring(0, Math.min(baseSlug.length(), 48));
        String candidate = baseSlug;
        while (organizationRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return candidate;
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            return java.util.HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 must be available", exception);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public record AuthenticationResult(AuthResponse response, String refreshToken) {
    }

    public record ActiveSessionResponse(
            UUID id,
            String deviceType,
            String browser,
            String operatingSystem,
            String ipAddress,
            boolean isCurrent,
            Instant lastActiveAt
    ) {
        public static ActiveSessionResponse fromEntity(ActiveSession session) {
            return new ActiveSessionResponse(
                    session.getId(),
                    session.getDeviceType(),
                    session.getBrowser(),
                    session.getOperatingSystem(),
                    session.getIpAddress(),
                    session.isCurrent(),
                    session.getLastActiveAt()
            );
        }
    }
}
