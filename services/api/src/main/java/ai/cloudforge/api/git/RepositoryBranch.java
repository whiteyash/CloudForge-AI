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
@Table(name = "repository_branches")
public class RepositoryBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "commit_sha", nullable = false, length = 40)
    private String commitSha;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(name = "is_protected")
    private boolean isProtected = false;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;

    protected RepositoryBranch() {
    }

    public RepositoryBranch(UUID repositoryId, String name, String commitSha, boolean isDefault, boolean isProtected) {
        this.repositoryId = repositoryId;
        this.name = name;
        this.commitSha = commitSha;
        this.isDefault = isDefault;
        this.isProtected = isProtected;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        lastUpdatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getName() {
        return name;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
