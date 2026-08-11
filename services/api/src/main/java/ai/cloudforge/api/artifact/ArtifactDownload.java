package ai.cloudforge.api.artifact;

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
@Table(name = "artifact_downloads")
public class ArtifactDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "artifact_id", nullable = false)
    private UUID artifactId;

    @Column(name = "downloaded_by", nullable = false, length = 100)
    private String downloadedBy;

    @Column(name = "downloaded_at", nullable = false, updatable = false)
    private Instant downloadedAt;

    protected ArtifactDownload() {
    }

    public ArtifactDownload(UUID artifactId, String downloadedBy) {
        this.artifactId = artifactId;
        this.downloadedBy = downloadedBy;
    }

    @PrePersist
    void onCreate() {
        if (downloadedAt == null) {
            downloadedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getArtifactId() {
        return artifactId;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public Instant getDownloadedAt() {
        return downloadedAt;
    }
}
