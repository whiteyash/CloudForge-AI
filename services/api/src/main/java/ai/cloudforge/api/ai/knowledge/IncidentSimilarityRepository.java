package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentSimilarityRepository extends JpaRepository<IncidentSimilarity, UUID> {

    List<IncidentSimilarity> findByProjectId(UUID projectId);
}
