package ai.cloudforge.api.project;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectVariableRepository extends JpaRepository<ProjectVariable, UUID> {

    List<ProjectVariable> findByProjectId(UUID projectId);
}
