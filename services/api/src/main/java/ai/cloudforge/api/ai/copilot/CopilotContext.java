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
@Table(name = "copilot_context")
public class CopilotContext {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "context_key", nullable = false, length = 100)
    private String contextKey;

    @Column(name = "context_value", nullable = false, columnDefinition = "TEXT")
    private String contextValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CopilotContext() {
    }

    public CopilotContext(UUID sessionId, String contextKey, String contextValue) {
        this.sessionId = sessionId;
        this.contextKey = contextKey;
        this.contextValue = contextValue;
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

    public UUID getSessionId() {
        return sessionId;
    }

    public String getContextKey() {
        return contextKey;
    }

    public String getContextValue() {
        return contextValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
