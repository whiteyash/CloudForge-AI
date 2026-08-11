package ai.cloudforge.api.ai.prediction;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacityForecastRepository extends JpaRepository<CapacityForecast, UUID> {

    List<CapacityForecast> findByProjectId(UUID projectId);
}
