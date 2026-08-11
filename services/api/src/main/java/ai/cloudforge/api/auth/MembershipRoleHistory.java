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
@Table(name = "membership_role_history")
public class MembershipRoleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "old_role", length = 40)
    private String oldRole;

    @Column(name = "new_role", nullable = false, length = 40)
    private String newRole;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MembershipRoleHistory() {
    }

    public MembershipRoleHistory(UUID orgId, UUID userId, String oldRole, String newRole, UUID changedBy) {
        this.orgId = orgId;
        this.userId = userId;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.changedBy = changedBy;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getOldRole() {
        return oldRole;
    }

    public String getNewRole() {
        return newRole;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
