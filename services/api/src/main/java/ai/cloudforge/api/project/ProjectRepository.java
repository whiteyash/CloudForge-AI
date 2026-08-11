package ai.cloudforge.api.project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOrganizationId(UUID orgId);

    Optional<Project> findByIdAndOrganizationId(UUID id, UUID orgId);

    boolean existsByOrganizationIdAndNameIgnoreCase(UUID orgId, String name);
}
