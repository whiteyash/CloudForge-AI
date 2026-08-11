package ai.cloudforge.api.environment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentVariableRepository extends JpaRepository<EnvironmentVariable, UUID> {

    List<EnvironmentVariable> findByEnvironmentId(UUID environmentId);
}
