package ai.cloudforge.api.job;

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
@Table(name = "job_logs")
public class JobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_execution_id", nullable = false)
    private UUID jobExecutionId;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Column(name = "log_line", nullable = false, columnDefinition = "TEXT")
    private String logLine;

    @Column(name = "stream_type", length = 20)
    private String streamType = "STDOUT";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected JobLog() {
    }

    public JobLog(UUID jobExecutionId, Integer sequenceNumber, String logLine, String streamType) {
        this.jobExecutionId = jobExecutionId;
        this.sequenceNumber = sequenceNumber;
        this.logLine = logLine;
        this.streamType = streamType != null ? streamType : "STDOUT";
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

    public UUID getJobExecutionId() {
        return jobExecutionId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getLogLine() {
        return logLine;
    }

    public String getStreamType() {
        return streamType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
