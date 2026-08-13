package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id")
    private UUID organizationId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String target;

    @Column(name = "environment", length = 20)
    private String environment = "DEV";

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(name = "ip_address", length = 45)
    private String ipAddress = "127.0.0.1";

    @Column(name = "metadata_json")
    private String metadataJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {
    }

    public AuditLog(UUID organizationId, UUID userId, String action, String target) {
        this(organizationId, userId, action, target, "DEV", null, null);
    }

    public AuditLog(UUID organizationId, UUID userId, String action, String target, String environment) {
        this(organizationId, userId, action, target, environment, null, null);
    }

    public AuditLog(UUID organizationId, UUID userId, String action, String target, String environment, String actorEmail, String metadataJson) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.action = action;
        this.target = target;
        this.environment = environment != null ? environment.toUpperCase() : "DEV";
        this.actorEmail = actorEmail;
        this.metadataJson = metadataJson;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
