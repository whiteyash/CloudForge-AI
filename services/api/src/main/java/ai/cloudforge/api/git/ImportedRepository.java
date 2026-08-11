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
@Table(name = "imported_repositories")
public class ImportedRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "connection_id")
    private UUID connectionId;

    @Column(name = "external_repo_id", nullable = false, length = 100)
    private String externalRepoId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    @Column(name = "clone_url", nullable = false, length = 255)
    private String cloneUrl;

    @Column(name = "default_branch", length = 100)
    private String defaultBranch = "main";

    @Column(length = 30)
    private String visibility = "PRIVATE";

    @Column(length = 50)
    private String language;

    @Column(name = "size_in_bytes")
    private Long sizeInBytes = 0L;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_private")
    private boolean isPrivate = true;

    @Column(name = "is_fork")
    private boolean isFork = false;

    @Column(name = "is_archived")
    private boolean isArchived = false;

    @Column(name = "ssh_url", length = 255)
    private String sshUrl;

    @Column(name = "web_url", length = 255)
    private String webUrl;

    @Column(name = "stargazers_count")
    private Integer stargazersCount = 0;

    @Column(name = "forks_count")
    private Integer forksCount = 0;

    @Column(name = "open_issues_count")
    private Integer openIssuesCount = 0;

    @Column(name = "pushed_at")
    private Instant pushedAt;

    @Column(name = "sync_status", length = 30)
    private String syncStatus = "SYNCHRONIZED";

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "last_synced_at", nullable = false)
    private Instant lastSyncedAt;

    @Column(name = "last_successful_sync_at", nullable = false)
    private Instant lastSuccessfulSyncAt;

    @Column(name = "last_failed_sync_at")
    private Instant lastFailedSyncAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ImportedRepository() {
    }

    public ImportedRepository(UUID projectId, UUID connectionId, String externalRepoId, String name, String fullName, String providerName, String cloneUrl, String defaultBranch, String visibility, String language) {
        this.projectId = projectId;
        this.connectionId = connectionId;
        this.externalRepoId = externalRepoId;
        this.name = name;
        this.fullName = fullName;
        this.providerName = providerName;
        this.cloneUrl = cloneUrl;
        this.defaultBranch = defaultBranch != null ? defaultBranch : "main";
        this.visibility = visibility != null ? visibility : "PRIVATE";
        this.language = language;
        this.webUrl = cloneUrl;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
        if (lastSyncedAt == null) {
            lastSyncedAt = Instant.now();
        }
        if (lastSuccessfulSyncAt == null) {
            lastSuccessfulSyncAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getConnectionId() {
        return connectionId;
    }

    public String getExternalRepoId() {
        return externalRepoId;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getLanguage() {
        return language;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isFork() {
        return isFork;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Integer getStargazersCount() {
        return stargazersCount;
    }

    public Integer getForksCount() {
        return forksCount;
    }

    public Integer getOpenIssuesCount() {
        return openIssuesCount;
    }

    public Instant getPushedAt() {
        return pushedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public Instant getLastSuccessfulSyncAt() {
        return lastSuccessfulSyncAt;
    }

    public Instant getLastFailedSyncAt() {
        return lastFailedSyncAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public void setLastSuccessfulSyncAt(Instant lastSuccessfulSyncAt) {
        this.lastSuccessfulSyncAt = lastSuccessfulSyncAt;
    }
}
