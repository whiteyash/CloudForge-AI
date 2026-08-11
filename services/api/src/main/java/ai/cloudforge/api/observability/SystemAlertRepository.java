package ai.cloudforge.api.observability;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemAlertRepository extends JpaRepository<SystemAlert, UUID> {

    List<SystemAlert> findByProjectId(UUID projectId);
}
