package ai.cloudforge.api.environment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentProfileRepository extends JpaRepository<EnvironmentProfile, UUID> {

    List<EnvironmentProfile> findByProjectId(UUID projectId);
}
