package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRecommendationRepository extends JpaRepository<IncidentRecommendation, UUID> {

    List<IncidentRecommendation> findByIncidentId(UUID incidentId);
}
