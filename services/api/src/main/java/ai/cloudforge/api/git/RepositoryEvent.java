package ai.cloudforge.api.git;

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
@Table(name = "repository_events")
public class RepositoryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "repository_id")
    private UUID repositoryId;

    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "delivery_id", nullable = false, length = 100)
    private String deliveryId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

    @Column(length = 30)
    private String status = "RECEIVED";

    @Column(name = "attempt_count")
    private Integer attemptCount = 1;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    protected RepositoryEvent() {
    }

    public RepositoryEvent(UUID projectId, UUID repositoryId, String providerName, String eventType, String deliveryId, String payloadHash) {
        this.projectId = projectId;
        this.repositoryId = repositoryId;
        this.providerName = providerName;
        this.eventType = eventType;
        this.deliveryId = deliveryId;
        this.correlationId = UUID.randomUUID();
        this.payloadHash = payloadHash;
    }

    @PrePersist
    void onCreate() {
        if (receivedAt == null) {
            receivedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public String getStatus() {
        return status;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setStatus(String status) {
        this.status = status;
        if ("PROCESSED".equals(status)) {
            this.processedAt = Instant.now();
        }
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
