package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerImageRepositoryRepository extends JpaRepository<ContainerImageRepository, UUID> {

    List<ContainerImageRepository> findByRegistryId(UUID registryId);

    List<ContainerImageRepository> findByProjectId(UUID projectId);

    Optional<ContainerImageRepository> findByIdAndRegistryId(UUID id, UUID registryId);

    Optional<ContainerImageRepository> findByRegistryIdAndRepositoryName(UUID registryId, String repositoryName);
}
