package ai.cloudforge.api.deployment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRollbackRepository extends JpaRepository<DeploymentRollback, UUID> {

    List<DeploymentRollback> findByDeploymentId(UUID deploymentId);
}
