package ai.cloudforge.api.aiops;

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
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String severity = "HIGH";

    @Column(length = 30)
    private String status = "OPEN";

    @Column(name = "environment", length = 30)
    private String environment = "DEV";

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "confidence_score")
    private Double confidenceScore = 0.85;

    @Column(name = "assignee_user_id")
    private UUID assigneeUserId;

    @Column(name = "incident_source", length = 50)
    private String incidentSource = "MONITORING";

    @Column(name = "detected_at", nullable = false, updatable = false)
    private Instant detectedAt;

    @Column(name = "triggered_at")
    private Instant triggeredAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public Incident() {
    }

    public Incident(UUID projectId, String title, String severity, String rootCause, Double confidenceScore) {
        this.projectId = projectId;
        this.title = title;
        this.severity = severity != null ? severity : "HIGH";
        this.rootCause = rootCause;
        this.confidenceScore = confidenceScore != null ? confidenceScore : 0.85;
    }

    public Incident(UUID organizationId, UUID projectId, String title, String severity, String description, String incidentSource) {
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.title = title;
        this.severity = severity != null ? severity : "HIGH";
        this.description = description;
        this.incidentSource = incidentSource != null ? incidentSource : "MONITORING";
    }

    @PrePersist
    void onCreate() {
        if (detectedAt == null) {
            detectedAt = Instant.now();
        }
        if (triggeredAt == null) {
            triggeredAt = detectedAt;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public UUID getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(UUID assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public String getIncidentSource() {
        return incidentSource;
    }

    public void setIncidentSource(String incidentSource) {
        this.incidentSource = incidentSource;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Instant detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Instant getTriggeredAt() {
        return triggeredAt != null ? triggeredAt : detectedAt;
    }

    public void setTriggeredAt(Instant triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void resolve() {
        this.status = "RESOLVED";
        this.resolvedAt = Instant.now();
    }
}
