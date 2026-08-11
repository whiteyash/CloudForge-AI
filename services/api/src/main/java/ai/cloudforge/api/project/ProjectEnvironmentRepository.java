package ai.cloudforge.api.project;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectEnvironmentRepository extends JpaRepository<ProjectEnvironment, UUID> {

    List<ProjectEnvironment> findByProjectId(UUID projectId);
}
