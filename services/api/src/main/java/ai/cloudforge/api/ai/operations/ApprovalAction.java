package ai.cloudforge.api.ai.operations;

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
@Table(name = "approval_actions")
public class ApprovalAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "approved_by", nullable = false)
    private UUID approvedBy;

    @Column(nullable = false, length = 30)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ApprovalAction() {
    }

    public ApprovalAction(UUID requestId, UUID approvedBy, String action, String comments) {
        this.requestId = requestId;
        this.approvedBy = approvedBy;
        this.action = action;
        this.comments = comments;
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

    public UUID getRequestId() {
        return requestId;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public String getAction() {
        return action;
    }

    public String getComments() {
        return comments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
