package ai.cloudforge.api.team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findByOrganizationId(UUID orgId);

    List<Team> findByOrganizationIdAndEnvironment(UUID orgId, String environment);

    Optional<Team> findByIdAndOrganizationId(UUID id, UUID orgId);

    boolean existsByOrganizationIdAndNameIgnoreCase(UUID orgId, String name);

    boolean existsByOrganizationIdAndEnvironmentAndNameIgnoreCase(UUID orgId, String environment, String name);
}
