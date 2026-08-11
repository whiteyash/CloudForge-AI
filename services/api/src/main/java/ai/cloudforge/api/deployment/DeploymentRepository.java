package ai.cloudforge.api.deployment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, UUID> {

    List<Deployment> findByProjectId(UUID projectId);

    Optional<Deployment> findByIdempotencyKey(String idempotencyKey);
}
