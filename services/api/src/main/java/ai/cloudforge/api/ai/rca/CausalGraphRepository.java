package ai.cloudforge.api.ai.rca;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CausalGraphRepository extends JpaRepository<CausalGraph, UUID> {

    List<CausalGraph> findByProjectId(UUID projectId);
}
