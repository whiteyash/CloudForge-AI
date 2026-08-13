package ai.cloudforge.api.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final AuditLogRepository auditLogRepository;

    public UserPreferencesService(
            UserPreferencesRepository userPreferencesRepository,
            NotificationPreferencesRepository notificationPreferencesRepository,
            AuditLogRepository auditLogRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.notificationPreferencesRepository = notificationPreferencesRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public UserPreferences getPreferences(UUID userId) {
        return userPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> userPreferencesRepository.save(new UserPreferences(userId)));
    }

    @Transactional
    public UserPreferences updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        UserPreferences prefs = getPreferences(userId);
        prefs.update(request.language(), request.timezone(), request.theme(), request.accentColor(), request.density());
        auditLogRepository.save(new AuditLog(null, userId, "user.preferences_updated", "preferences"));
        return userPreferencesRepository.save(prefs);
    }

    @Transactional(readOnly = true)
    public NotificationPreferences getNotificationPreferences(UUID userId) {
        return notificationPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> notificationPreferencesRepository.save(new NotificationPreferences(userId)));
    }

    @Transactional
    public NotificationPreferences updateNotificationPreferences(UUID userId, UpdateNotificationPreferencesRequest request) {
        NotificationPreferences prefs = getNotificationPreferences(userId);
        prefs.update(
                request.emailSecurityAlerts(),
                request.emailOrgEvents(),
                request.emailInvitations(),
                request.emailRoleChanges(),
                request.inappSecurityAlerts(),
                request.inappOrgEvents(),
                request.inappInvitations(),
                request.inappRoleChanges()
        );
        auditLogRepository.save(new AuditLog(null, userId, "user.notification_preferences_updated", "notification_preferences"));
        return notificationPreferencesRepository.save(prefs);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getPersonalAuditTrail(UUID userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public record UpdatePreferencesRequest(
            String language,
            String timezone,
            String theme,
            String accentColor,
            String density
    ) {}

    public record UpdateNotificationPreferencesRequest(
            Boolean emailSecurityAlerts,
            Boolean emailOrgEvents,
            Boolean emailInvitations,
            Boolean emailRoleChanges,
            Boolean inappSecurityAlerts,
            Boolean inappOrgEvents,
            Boolean inappInvitations,
            Boolean inappRoleChanges
    ) {}
}
