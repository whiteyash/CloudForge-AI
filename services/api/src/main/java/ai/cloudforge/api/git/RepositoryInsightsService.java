package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepositoryInsightsService {

    private final RepositoryBranchRepository branchRepository;
    private final RepositoryCommitRepository commitRepository;
    private final RepositoryContributorRepository contributorRepository;
    private final RepositoryPullRequestRepository prRepository;
    private final RepositoryInsightsSnapshotRepository snapshotRepository;

    public RepositoryInsightsService(
            RepositoryBranchRepository branchRepository,
            RepositoryCommitRepository commitRepository,
            RepositoryContributorRepository contributorRepository,
            RepositoryPullRequestRepository prRepository,
            RepositoryInsightsSnapshotRepository snapshotRepository) {
        this.branchRepository = branchRepository;
        this.commitRepository = commitRepository;
        this.contributorRepository = contributorRepository;
        this.prRepository = prRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional(readOnly = true)
    public InsightsSummary getInsightsForRepository(UUID repositoryId) {
        int totalBranches = branchRepository.findByRepositoryId(repositoryId).size();
        int totalCommits = commitRepository.findByRepositoryIdOrderByCommittedAtDesc(repositoryId).size();
        int totalContributors = contributorRepository.findByRepositoryId(repositoryId).size();

        List<RepositoryPullRequest> prs = prRepository.findByRepositoryIdOrderByCreatedAtDesc(repositoryId);
        int openPrs = (int) prs.stream().filter(pr -> "OPEN".equals(pr.getState())).count();
        int mergedPrs = (int) prs.stream().filter(pr -> "MERGED".equals(pr.getState())).count();
        int closedPrs = (int) prs.stream().filter(pr -> "CLOSED".equals(pr.getState())).count();

        int healthScore = calculateHealthScore(totalBranches, totalCommits, totalContributors, openPrs);

        return new InsightsSummary(
                repositoryId, totalBranches, totalCommits, totalContributors,
                openPrs, mergedPrs, closedPrs, healthScore
        );
    }

    public int calculateHealthScore(int branches, int commits, int contributors, int openPrs) {
        int score = 100;
        if (openPrs > 10) score -= 15;
        if (branches > 20) score -= 10;
        if (contributors == 0) score -= 30;
        if (commits == 0) score -= 25;
        return Math.max(score, 0);
    }

    public record InsightsSummary(
            UUID repositoryId,
            int totalBranches,
            int totalCommits,
            int totalContributors,
            int openPrs,
            int mergedPrs,
            int closedPrs,
            int healthScore
    ) {}
}
