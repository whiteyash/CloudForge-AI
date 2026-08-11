package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntentHistoryRepository extends JpaRepository<IntentHistory, UUID> {

    List<IntentHistory> findByProjectId(UUID projectId);
}
