package ai.cloudforge.api.ai.log;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogClusterRepository extends JpaRepository<LogCluster, UUID> {

    List<LogCluster> findByProjectId(UUID projectId);
}
