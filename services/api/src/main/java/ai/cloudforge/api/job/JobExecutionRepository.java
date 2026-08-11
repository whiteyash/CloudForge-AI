package ai.cloudforge.api.job;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, UUID> {

    List<JobExecution> findByPipelineRunId(UUID pipelineRunId);
}
