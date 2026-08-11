package ai.cloudforge.api.profile;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AppUser;
import ai.cloudforge.api.auth.AppUserRepository;
import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;

import ai.cloudforge.api.auth.ResourceNotFoundException;

@Service
public class UserProfileService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    public UserProfileService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }

        auditLogRepository.save(new AuditLog(null, userId, "user.profile_updated", user.getEmail()));
        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getHashedPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setHashedPassword(passwordEncoder.encode(request.newPassword()));
        auditLogRepository.save(new AuditLog(null, userId, "user.password_changed", user.getEmail()));
    }

    public record UserProfileResponse(
            UUID id,
            String email,
            String fullName,
            Instant createdAt
    ) {
        public static UserProfileResponse fromEntity(AppUser user) {
            return new UserProfileResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getCreatedAt()
            );
        }
    }

    public record UpdateProfileRequest(
            String fullName,
            String avatarUrl,
            String timezone,
            String language,
            String themePreference
    ) {}

    public record ChangePasswordRequest(
            String oldPassword,
            String newPassword
    ) {}
}
