package ai.cloudforge.api.ai.knowledge;

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
@Table(name = "ai_runbooks")
public class AIRunbook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 30)
    private String version = "1.0";

    @Column(name = "success_rate")
    private int successRate = 95;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AIRunbook() {
    }

    public AIRunbook(UUID projectId, String title, String category, String content, String version, int successRate) {
        this.projectId = projectId;
        this.title = title;
        this.category = category;
        this.content = content;
        this.version = version != null ? version : "1.0";
        this.successRate = successRate;
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

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getVersion() {
        return version;
    }

    public int getSuccessRate() {
        return successRate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
