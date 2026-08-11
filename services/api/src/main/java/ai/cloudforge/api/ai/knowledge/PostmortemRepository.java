package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostmortemRepository extends JpaRepository<Postmortem, UUID> {

    List<Postmortem> findByProjectId(UUID projectId);
}
