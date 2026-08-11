package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "active_sessions")
public class ActiveSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "session_token", nullable = false, unique = true, length = 64)
    private String sessionToken;

    @Column(name = "device_type", length = 50)
    private String deviceType = "Desktop";

    @Column(length = 100)
    private String browser = "Unknown Browser";

    @Column(name = "operating_system", length = 100)
    private String operatingSystem = "Unknown OS";

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String geolocation;

    @Column(name = "is_current")
    private boolean current = false;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ActiveSession() {
    }

    public ActiveSession(AppUser user, String sessionToken, String deviceType, String browser, String operatingSystem, String ipAddress, boolean current) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.deviceType = deviceType;
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.ipAddress = ipAddress;
        this.current = current;
        this.lastActiveAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (lastActiveAt == null) {
            lastActiveAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public boolean isCurrent() {
        return current;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void touch() {
        this.lastActiveAt = Instant.now();
    }
}
