package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryGovernancePolicyRepository extends JpaRepository<RepositoryGovernancePolicy, UUID> {

    Optional<RepositoryGovernancePolicy> findByRepositoryId(UUID repositoryId);
}
