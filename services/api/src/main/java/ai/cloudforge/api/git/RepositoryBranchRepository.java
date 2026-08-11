package ai.cloudforge.api.git;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryBranchRepository extends JpaRepository<RepositoryBranch, UUID> {

    List<RepositoryBranch> findByRepositoryId(UUID repositoryId);

    Optional<RepositoryBranch> findByRepositoryIdAndName(UUID repositoryId, String name);
}
