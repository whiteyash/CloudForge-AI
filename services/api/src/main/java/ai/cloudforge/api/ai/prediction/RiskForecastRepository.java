package ai.cloudforge.api.ai.prediction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskForecastRepository extends JpaRepository<RiskForecast, UUID> {

    List<RiskForecast> findByProjectId(UUID projectId);
}
