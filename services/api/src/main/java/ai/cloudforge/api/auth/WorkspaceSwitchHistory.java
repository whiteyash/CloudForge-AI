package ai.cloudforge.api.auth;

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
@Table(name = "workspace_switch_history")
public class WorkspaceSwitchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "from_org_id")
    private UUID fromOrgId;

    @Column(name = "to_org_id", nullable = false)
    private UUID toOrgId;

    @Column(name = "switched_at", nullable = false, updatable = false)
    private Instant switchedAt;

    protected WorkspaceSwitchHistory() {
    }

    public WorkspaceSwitchHistory(UUID userId, UUID fromOrgId, UUID toOrgId) {
        this.userId = userId;
        this.fromOrgId = fromOrgId;
        this.toOrgId = toOrgId;
    }

    @PrePersist
    void onCreate() {
        if (switchedAt == null) {
            switchedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getFromOrgId() {
        return fromOrgId;
    }

    public UUID getToOrgId() {
        return toOrgId;
    }

    public Instant getSwitchedAt() {
        return switchedAt;
    }
}
