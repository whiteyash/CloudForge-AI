package ai.cloudforge.api.git;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportedRepositoryRepository extends JpaRepository<ImportedRepository, UUID> {

    List<ImportedRepository> findByProjectId(UUID projectId);

    Optional<ImportedRepository> findByProjectIdAndExternalRepoId(UUID projectId, String externalRepoId);
}
