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
@Table(name = "exception_fingerprints")
public class ExceptionFingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "fingerprint_hash", nullable = false, length = 64)
    private String fingerprintHash;

    @Column(name = "exception_class", nullable = false, length = 255)
    private String exceptionClass;

    @Column(name = "failed_method", nullable = false, length = 150)
    private String failedMethod;

    @Column(name = "failed_file", nullable = false, length = 255)
    private String failedFile;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ExceptionFingerprint() {
    }

    public ExceptionFingerprint(UUID projectId, String fingerprintHash, String exceptionClass, String failedMethod, String failedFile, int lineNumber) {
        this.projectId = projectId;
        this.fingerprintHash = fingerprintHash;
        this.exceptionClass = exceptionClass;
        this.failedMethod = failedMethod;
        this.failedFile = failedFile;
        this.lineNumber = lineNumber;
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

    public String getFingerprintHash() {
        return fingerprintHash;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getFailedMethod() {
        return failedMethod;
    }

    public String getFailedFile() {
        return failedFile;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
