package ai.cloudforge.api.project;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSecretReferenceRepository extends JpaRepository<ProjectSecretReference, UUID> {

    List<ProjectSecretReference> findByProjectId(UUID projectId);
}
