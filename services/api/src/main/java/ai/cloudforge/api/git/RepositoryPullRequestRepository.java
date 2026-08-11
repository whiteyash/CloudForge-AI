package ai.cloudforge.api.git;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryPullRequestRepository extends JpaRepository<RepositoryPullRequest, UUID> {

    List<RepositoryPullRequest> findByRepositoryIdOrderByCreatedAtDesc(UUID repositoryId);

    Optional<RepositoryPullRequest> findByRepositoryIdAndNumber(UUID repositoryId, Integer number);
}
