package ai.cloudforge.api.aiops;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRiskRepository extends JpaRepository<DeploymentRiskAssessment, UUID> {

    Optional<DeploymentRiskAssessment> findByDeploymentId(UUID deploymentId);
}
