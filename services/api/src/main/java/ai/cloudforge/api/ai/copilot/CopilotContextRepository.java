package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CopilotContextRepository extends JpaRepository<CopilotContext, UUID> {

    List<CopilotContext> findBySessionId(UUID sessionId);
}
