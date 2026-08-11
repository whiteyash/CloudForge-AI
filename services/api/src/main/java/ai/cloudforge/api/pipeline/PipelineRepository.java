package ai.cloudforge.api.pipeline;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    List<Pipeline> findByProjectId(UUID projectId);
}
