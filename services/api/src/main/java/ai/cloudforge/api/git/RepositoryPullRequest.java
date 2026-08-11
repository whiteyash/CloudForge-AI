package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "repository_pull_requests")
public class RepositoryPullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "external_pr_id", nullable = false, length = 100)
    private String externalPrId;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String state = "OPEN";

    @Column(name = "author_username", nullable = false, length = 100)
    private String authorUsername;

    @Column(name = "author_avatar_url", length = 255)
    private String authorAvatarUrl;

    @Column(name = "source_branch", nullable = false, length = 150)
    private String sourceBranch;

    @Column(name = "target_branch", nullable = false, length = 150)
    private String targetBranch;

    @Column(name = "is_draft")
    private boolean isDraft = false;

    @Column(name = "web_url", length = 255)
    private String webUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "merged_at")
    private Instant mergedAt;

    protected RepositoryPullRequest() {
    }

    public RepositoryPullRequest(UUID repositoryId, String externalPrId, Integer number, String title, String description, String state, String authorUsername, String authorAvatarUrl, String sourceBranch, String targetBranch, boolean isDraft, String webUrl) {
        this.repositoryId = repositoryId;
        this.externalPrId = externalPrId;
        this.number = number;
        this.title = title;
        this.description = description;
        this.state = state != null ? state : "OPEN";
        this.authorUsername = authorUsername;
        this.authorAvatarUrl = authorAvatarUrl;
        this.sourceBranch = sourceBranch;
        this.targetBranch = targetBranch;
        this.isDraft = isDraft;
        this.webUrl = webUrl;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
        if ("MERGED".equals(state) && mergedAt == null) {
            mergedAt = Instant.now();
        }
        if ("CLOSED".equals(state) && closedAt == null) {
            closedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getExternalPrId() {
        return externalPrId;
    }

    public Integer getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public Instant getMergedAt() {
        return mergedAt;
    }
}
