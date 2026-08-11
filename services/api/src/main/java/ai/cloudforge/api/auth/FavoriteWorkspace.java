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
@Table(name = "favorite_workspaces")
public class FavoriteWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "is_pinned")
    private boolean isPinned = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected FavoriteWorkspace() {
    }

    public FavoriteWorkspace(UUID userId, UUID orgId, boolean isPinned) {
        this.userId = userId;
        this.orgId = orgId;
        this.isPinned = isPinned;
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

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
