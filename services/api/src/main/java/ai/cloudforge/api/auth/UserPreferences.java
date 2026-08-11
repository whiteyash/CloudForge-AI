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
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(length = 10)
    private String language = "en";

    @Column(length = 50)
    private String timezone = "UTC";

    @Column(name = "date_format", length = 20)
    private String dateFormat = "YYYY-MM-DD";

    @Column(name = "time_format", length = 10)
    private String timeFormat = "24h";

    @Column(length = 20)
    private String theme = "DARK_SLATE";

    @Column(name = "accent_color", length = 20)
    private String accentColor = "#3DD9C4";

    @Column(length = 20)
    private String density = "COMFORTABLE";

    @Column(name = "default_workspace_id")
    private UUID defaultWorkspaceId;

    @Column(name = "default_landing_page", length = 100)
    private String defaultLandingPage = "/";

    @Column(name = "sidebar_collapsed")
    private boolean sidebarCollapsed = false;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserPreferences() {
    }

    public UserPreferences(UUID userId) {
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

    public String getLanguage() {
        return language;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public String getTheme() {
        return theme;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public String getDensity() {
        return density;
    }

    public UUID getDefaultWorkspaceId() {
        return defaultWorkspaceId;
    }

    public String getDefaultLandingPage() {
        return defaultLandingPage;
    }

    public boolean isSidebarCollapsed() {
        return sidebarCollapsed;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(String language, String timezone, String theme, String accentColor, String density) {
        if (language != null) this.language = language;
        if (timezone != null) this.timezone = timezone;
        if (theme != null) this.theme = theme;
        if (accentColor != null) this.accentColor = accentColor;
        if (density != null) this.density = density;
    }
}
