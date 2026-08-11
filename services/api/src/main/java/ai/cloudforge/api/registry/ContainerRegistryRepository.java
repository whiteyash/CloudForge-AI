package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRegistryRepository extends JpaRepository<ContainerRegistry, UUID> {

    List<ContainerRegistry> findByProjectId(UUID projectId);

    Optional<ContainerRegistry> findByIdAndProjectId(UUID id, UUID projectId);

    boolean existsByProjectIdAndName(UUID projectId, String name);
}
