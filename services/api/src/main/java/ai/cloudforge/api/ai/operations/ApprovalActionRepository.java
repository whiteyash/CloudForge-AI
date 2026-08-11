package ai.cloudforge.api.ai.operations;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalActionRepository extends JpaRepository<ApprovalAction, UUID> {

    List<ApprovalAction> findByRequestId(UUID requestId);
}
