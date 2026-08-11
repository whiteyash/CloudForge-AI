package ai.cloudforge.api.ai.operations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, UUID> {

    Optional<ApprovalRequest> findByPlanId(UUID planId);

    List<ApprovalRequest> findByStatus(String status);
}
