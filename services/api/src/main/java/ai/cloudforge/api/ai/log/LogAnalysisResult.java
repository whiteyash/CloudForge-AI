package ai.cloudforge.api.ai.log;

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
@Table(name = "log_analysis_results")
public class LogAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "log_entry_id")
    private UUID logEntryId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "root_cause", nullable = false, columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "confidence_score", nullable = false)
    private int confidenceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LogAnalysisResult() {
    }

    public LogAnalysisResult(UUID projectId, UUID logEntryId, String summary, String rootCause, int confidenceScore) {
        this.projectId = projectId;
        this.logEntryId = logEntryId;
        this.summary = summary;
        this.rootCause = rootCause;
        this.confidenceScore = confidenceScore;
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

    public UUID getLogEntryId() {
        return logEntryId;
    }

    public String getSummary() {
        return summary;
    }

    public String getRootCause() {
        return rootCause;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
