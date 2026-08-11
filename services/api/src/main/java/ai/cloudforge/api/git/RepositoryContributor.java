package ai.cloudforge.api.git;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "repository_contributors")
public class RepositoryContributor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "contribution_count")
    private Integer contributionCount = 1;

    protected RepositoryContributor() {
    }

    public RepositoryContributor(UUID repositoryId, String username, String displayName, String avatarUrl, Integer contributionCount) {
        this.repositoryId = repositoryId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.contributionCount = contributionCount != null ? contributionCount : 1;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Integer getContributionCount() {
        return contributionCount;
    }
}
