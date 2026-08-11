package ai.cloudforge.api.ai.prediction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictiveForecastRepository extends JpaRepository<PredictiveForecast, UUID> {

    List<PredictiveForecast> findByProjectId(UUID projectId);
}
