package ai.cloudforge.api.runner;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerAssignmentRepository extends JpaRepository<RunnerAssignment, UUID> {

    List<RunnerAssignment> findByRunnerId(UUID runnerId);
}
