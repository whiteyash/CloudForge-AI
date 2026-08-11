package ai.cloudforge.api.git;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "repository_insights_snapshots")
public class RepositoryInsightsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "repository_id", nullable = false)
    private UUID repositoryId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "total_commits")
    private Integer totalCommits = 0;

    @Column(name = "total_branches")
    private Integer totalBranches = 0;

    @Column(name = "total_contributors")
    private Integer totalContributors = 0;

    @Column(name = "open_prs")
    private Integer openPrs = 0;

    @Column(name = "merged_prs")
    private Integer mergedPrs = 0;

    @Column(name = "closed_prs")
    private Integer closedPrs = 0;

    @Column(name = "health_score")
    private Integer healthScore = 100;

    protected RepositoryInsightsSnapshot() {
    }

    public RepositoryInsightsSnapshot(UUID repositoryId, LocalDate snapshotDate, Integer totalCommits, Integer totalBranches, Integer totalContributors, Integer openPrs, Integer mergedPrs, Integer closedPrs, Integer healthScore) {
        this.repositoryId = repositoryId;
        this.snapshotDate = snapshotDate;
        this.totalCommits = totalCommits;
        this.totalBranches = totalBranches;
        this.totalContributors = totalContributors;
        this.openPrs = openPrs;
        this.mergedPrs = mergedPrs;
        this.closedPrs = closedPrs;
        this.healthScore = healthScore != null ? healthScore : 100;
    }

    @PrePersist
    void onCreate() {
        if (snapshotDate == null) {
            snapshotDate = LocalDate.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public Integer getTotalCommits() {
        return totalCommits;
    }

    public Integer getTotalBranches() {
        return totalBranches;
    }

    public Integer getTotalContributors() {
        return totalContributors;
    }

    public Integer getOpenPrs() {
        return openPrs;
    }

    public Integer getMergedPrs() {
        return mergedPrs;
    }

    public Integer getClosedPrs() {
        return closedPrs;
    }

    public Integer getHealthScore() {
        return healthScore;
    }
}
