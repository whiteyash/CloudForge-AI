package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerImageTagRepository extends JpaRepository<ContainerImageTag, UUID> {

    List<ContainerImageTag> findByRepositoryIdOrderByPushedAtDesc(UUID repositoryId);

    Optional<ContainerImageTag> findByIdAndRepositoryId(UUID id, UUID repositoryId);

    Optional<ContainerImageTag> findByRepositoryIdAndTagName(UUID repositoryId, String tagName);
}
