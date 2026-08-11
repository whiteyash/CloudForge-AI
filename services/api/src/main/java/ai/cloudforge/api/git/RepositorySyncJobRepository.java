package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorySyncJobRepository extends JpaRepository<RepositorySyncJob, UUID> {

    List<RepositorySyncJob> findByRepositoryIdOrderByStartedAtDesc(UUID repositoryId);
}
