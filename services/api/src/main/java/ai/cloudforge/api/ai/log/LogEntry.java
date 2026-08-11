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
@Table(name = "log_entries")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    @Column(length = 30)
    private String severity = "ERROR";

    @Column(name = "log_message", nullable = false, columnDefinition = "TEXT")
    private String logMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    protected LogEntry() {
    }

    public LogEntry(UUID projectId, String sourceType, String severity, String logMessage, String stackTrace) {
        this.projectId = projectId;
        this.sourceType = sourceType;
        this.severity = severity != null ? severity : "ERROR";
        this.logMessage = logMessage;
        this.stackTrace = stackTrace;
    }

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
