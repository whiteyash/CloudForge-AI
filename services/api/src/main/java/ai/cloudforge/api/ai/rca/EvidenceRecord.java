package ai.cloudforge.api.ai.rca;

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
@Table(name = "evidence_records")
public class EvidenceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "root_cause_report_id")
    private UUID rootCauseReportId;

    @Column(name = "source_system", nullable = false, length = 50)
    private String sourceSystem;

    @Column(name = "evidence_text", nullable = false, columnDefinition = "TEXT")
    private String evidenceText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected EvidenceRecord() {
    }

    public EvidenceRecord(UUID projectId, UUID rootCauseReportId, String sourceSystem, String evidenceText) {
        this.projectId = projectId;
        this.rootCauseReportId = rootCauseReportId;
        this.sourceSystem = sourceSystem;
        this.evidenceText = evidenceText;
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

    public UUID getRootCauseReportId() {
        return rootCauseReportId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getEvidenceText() {
        return evidenceText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
