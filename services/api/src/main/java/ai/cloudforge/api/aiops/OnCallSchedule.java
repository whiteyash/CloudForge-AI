package ai.cloudforge.api.aiops;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "oncall_schedules")
public class OnCallSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone;

    @Column(name = "rotation_type", nullable = false, length = 30)
    private String rotationType;

    @Column(name = "active_user_id", nullable = false)
    private UUID activeUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public OnCallSchedule() {}

    public OnCallSchedule(UUID organizationId, String name, String timeZone, String rotationType, UUID activeUserId) {
        this.organizationId = organizationId;
        this.name = name;
        this.timeZone = timeZone != null ? timeZone : "UTC";
        this.rotationType = rotationType != null ? rotationType : "WEEKLY";
        this.activeUserId = activeUserId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public String getRotationType() { return rotationType; }
    public void setRotationType(String rotationType) { this.rotationType = rotationType; }

    public UUID getActiveUserId() { return activeUserId; }
    public void setActiveUserId(UUID activeUserId) { this.activeUserId = activeUserId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
