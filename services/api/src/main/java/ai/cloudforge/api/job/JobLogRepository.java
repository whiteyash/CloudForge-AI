package ai.cloudforge.api.job;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, UUID> {

    List<JobLog> findByJobExecutionIdOrderBySequenceNumberAsc(UUID jobExecutionId);
}
