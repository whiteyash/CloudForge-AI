package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email_security_alerts")
    private boolean emailSecurityAlerts = true;

    @Column(name = "email_org_events")
    private boolean emailOrgEvents = true;

    @Column(name = "email_invitations")
    private boolean emailInvitations = true;

    @Column(name = "email_role_changes")
    private boolean emailRoleChanges = true;

    @Column(name = "inapp_security_alerts")
    private boolean inappSecurityAlerts = true;

    @Column(name = "inapp_org_events")
    private boolean inappOrgEvents = true;

    @Column(name = "inapp_invitations")
    private boolean inappInvitations = true;

    @Column(name = "inapp_role_changes")
    private boolean inappRoleChanges = true;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected NotificationPreferences() {
    }

    public NotificationPreferences(UUID userId) {
        this.userId = userId;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isEmailSecurityAlerts() {
        return emailSecurityAlerts;
    }

    public boolean isEmailOrgEvents() {
        return emailOrgEvents;
    }

    public boolean isEmailInvitations() {
        return emailInvitations;
    }

    public boolean isEmailRoleChanges() {
        return emailRoleChanges;
    }

    public boolean isInappSecurityAlerts() {
        return inappSecurityAlerts;
    }

    public boolean isInappOrgEvents() {
        return inappOrgEvents;
    }

    public boolean isInappInvitations() {
        return inappInvitations;
    }

    public boolean isInappRoleChanges() {
        return inappRoleChanges;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(
            Boolean emailSecurityAlerts,
            Boolean emailOrgEvents,
            Boolean emailInvitations,
            Boolean emailRoleChanges,
            Boolean inappSecurityAlerts,
            Boolean inappOrgEvents,
            Boolean inappInvitations,
            Boolean inappRoleChanges) {
        if (emailSecurityAlerts != null) this.emailSecurityAlerts = emailSecurityAlerts;
        if (emailOrgEvents != null) this.emailOrgEvents = emailOrgEvents;
        if (emailInvitations != null) this.emailInvitations = emailInvitations;
        if (emailRoleChanges != null) this.emailRoleChanges = emailRoleChanges;
        if (inappSecurityAlerts != null) this.inappSecurityAlerts = inappSecurityAlerts;
        if (inappOrgEvents != null) this.inappOrgEvents = inappOrgEvents;
        if (inappInvitations != null) this.inappInvitations = inappInvitations;
        if (inappRoleChanges != null) this.inappRoleChanges = inappRoleChanges;
    }
}
