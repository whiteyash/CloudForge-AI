package ai.cloudforge.api.runner;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, UUID> {

    List<Runner> findByProjectId(UUID projectId);
}
