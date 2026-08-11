package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    List<Incident> findByProjectId(UUID projectId);

    List<Incident> findByProjectIdAndEnvironment(UUID projectId, String environment);

    Optional<Incident> findByProjectIdAndId(UUID projectId, UUID id);
}
