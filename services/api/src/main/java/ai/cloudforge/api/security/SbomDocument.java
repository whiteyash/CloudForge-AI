package ai.cloudforge.api.security;

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
@Table(name = "sbom_documents")
public class SbomDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "target_name", nullable = false)
    private String targetName;

    @Column(name = "format", nullable = false, length = 30)
    private String format;

    @Column(name = "spec_version", nullable = false, length = 20)
    private String specVersion;

    @Column(name = "total_components", nullable = false)
    private int totalComponents;

    @Column(name = "document_json", nullable = false, columnDefinition = "TEXT")
    private String documentJson;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    public SbomDocument() {}

    public SbomDocument(UUID projectId, String targetName, String format, String specVersion, int totalComponents, String documentJson) {
        this.projectId = projectId;
        this.targetName = targetName;
        this.format = format != null ? format : "SPDX_2_3";
        this.specVersion = specVersion != null ? specVersion : "2.3";
        this.totalComponents = totalComponents;
        this.documentJson = documentJson;
    }

    @PrePersist
    protected void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getSpecVersion() { return specVersion; }
    public void setSpecVersion(String specVersion) { this.specVersion = specVersion; }

    public int getTotalComponents() { return totalComponents; }
    public void setTotalComponents(int totalComponents) { this.totalComponents = totalComponents; }

    public String getDocumentJson() { return documentJson; }
    public void setDocumentJson(String documentJson) { this.documentJson = documentJson; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
}
