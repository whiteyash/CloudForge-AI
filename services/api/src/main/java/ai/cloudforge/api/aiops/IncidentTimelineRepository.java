package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentTimelineRepository extends JpaRepository<IncidentTimelineEvent, UUID> {

    List<IncidentTimelineEvent> findByIncidentIdOrderByTimestampAsc(UUID incidentId);
}
