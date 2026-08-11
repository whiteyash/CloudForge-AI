package ai.cloudforge.api.ai.copilot;

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
@Table(name = "ai_intent_history")
public class IntentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "intent_type", nullable = false, length = 100)
    private String intentType;

    @Column(name = "raw_prompt", nullable = false, columnDefinition = "TEXT")
    private String rawPrompt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected IntentHistory() {
    }

    public IntentHistory(UUID projectId, UUID userId, String intentType, String rawPrompt) {
        this.projectId = projectId;
        this.userId = userId;
        this.intentType = intentType;
        this.rawPrompt = rawPrompt;
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

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getIntentType() {
        return intentType;
    }

    public String getRawPrompt() {
        return rawPrompt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
