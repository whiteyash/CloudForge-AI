package ai.cloudforge.api.ai.operations;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemediationPlanRepository extends JpaRepository<RemediationPlan, UUID> {

    List<RemediationPlan> findByProjectId(UUID projectId);
}
