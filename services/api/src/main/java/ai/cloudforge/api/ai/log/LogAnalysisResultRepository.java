package ai.cloudforge.api.ai.log;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAnalysisResultRepository extends JpaRepository<LogAnalysisResult, UUID> {

    List<LogAnalysisResult> findByProjectId(UUID projectId);
}
