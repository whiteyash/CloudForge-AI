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
@Table(name = "repository_releases")
public class RepositoryRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "external_release_id", nullable = false, length = 100)
    private String externalReleaseId;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "author_username", nullable = false, length = 100)
    private String authorUsername;

    @Column(name = "author_avatar_url", length = 255)
    private String authorAvatarUrl;

    @Column(name = "is_draft")
    private boolean isDraft = false;

    @Column(name = "is_prerelease")
    private boolean isPrerelease = false;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "web_url", length = 255)
    private String webUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RepositoryRelease() {
    }

    public RepositoryRelease(UUID repositoryId, String externalReleaseId, String tagName, String name, String body, String authorUsername, String authorAvatarUrl, boolean isDraft, boolean isPrerelease, Instant publishedAt, String webUrl) {
        this.repositoryId = repositoryId;
        this.externalReleaseId = externalReleaseId;
        this.tagName = tagName;
        this.name = name;
        this.body = body;
        this.authorUsername = authorUsername;
        this.authorAvatarUrl = authorAvatarUrl;
        this.isDraft = isDraft;
        this.isPrerelease = isPrerelease;
        this.publishedAt = publishedAt != null ? publishedAt : Instant.now();
        this.webUrl = webUrl;
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

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getExternalReleaseId() {
        return externalReleaseId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public boolean isPrerelease() {
        return isPrerelease;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
