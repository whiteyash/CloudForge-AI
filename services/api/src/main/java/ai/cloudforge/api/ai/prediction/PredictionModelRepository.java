package ai.cloudforge.api.ai.prediction;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionModelRepository extends JpaRepository<PredictionModel, UUID> {
}
