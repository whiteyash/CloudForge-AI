package ai.cloudforge.api.registry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NativeImageBuildRepository extends JpaRepository<NativeImageBuild, UUID> {

    List<NativeImageBuild> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    Optional<NativeImageBuild> findByIdAndProjectId(UUID id, UUID projectId);
}
