package ai.cloudforge.api.registry;

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
@Table(name = "container_image_tags")
public class ContainerImageTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "tag_name", nullable = false, length = 128)
    private String tagName;

    @Column(name = "digest_sha256", nullable = true, length = 128)
    private String digestSha256;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes = 0L;

    @Column(nullable = false, length = 50)
    private String architecture = "linux/amd64";

    @Column(name = "is_immutable", nullable = false)
    private Boolean isImmutable = false;

    @Column(name = "pushed_at", nullable = false)
    private Instant pushedAt;

    public ContainerImageTag() {
    }

    public ContainerImageTag(UUID repositoryId, String tagName, String digestSha256, Long sizeBytes, String architecture, Boolean isImmutable) {
        this.repositoryId = repositoryId;
        this.tagName = tagName;
        this.digestSha256 = digestSha256;
        this.sizeBytes = sizeBytes;
        this.architecture = (architecture != null && !architecture.isBlank()) ? architecture : "linux/amd64";
        this.isImmutable = (isImmutable != null) && isImmutable;
        this.pushedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.pushedAt == null) {
            this.pushedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(UUID repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDigestSha256() {
        return digestSha256;
    }

    public void setDigestSha256(String digestSha256) {
        this.digestSha256 = digestSha256;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Boolean getIsImmutable() {
        return isImmutable;
    }

    public void setIsImmutable(Boolean isImmutable) {
        this.isImmutable = isImmutable;
    }

    public Instant getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(Instant pushedAt) {
        this.pushedAt = pushedAt;
    }
}
