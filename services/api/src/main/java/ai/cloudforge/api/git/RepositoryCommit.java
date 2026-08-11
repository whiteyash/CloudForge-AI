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
@Table(name = "repository_commits")
public class RepositoryCommit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "commit_sha", nullable = false, length = 40)
    private String commitSha;

    @Column(name = "short_sha", nullable = false, length = 10)
    private String shortSha;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(name = "author_email", length = 100)
    private String authorEmail;

    @Column(name = "committer_name", length = 100)
    private String committerName;

    @Column(name = "committer_email", length = 100)
    private String committerEmail;

    @Column(name = "committed_at", nullable = false)
    private Instant committedAt;

    @Column(name = "web_url", length = 255)
    private String webUrl;

    protected RepositoryCommit() {
    }

    public RepositoryCommit(UUID repositoryId, String commitSha, String message, String authorName, String authorEmail, Instant committedAt, String webUrl) {
        this.repositoryId = repositoryId;
        this.commitSha = commitSha;
        this.shortSha = commitSha != null && commitSha.length() >= 7 ? commitSha.substring(0, 7) : commitSha;
        this.message = message;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.committedAt = committedAt != null ? committedAt : Instant.now();
        this.webUrl = webUrl;
    }

    @PrePersist
    void onCreate() {
        if (committedAt == null) {
            committedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public String getShortSha() {
        return shortSha;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public Instant getCommittedAt() {
        return committedAt;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
