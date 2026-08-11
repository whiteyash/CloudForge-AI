package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryWebhookRepository extends JpaRepository<RepositoryWebhook, UUID> {

    List<RepositoryWebhook> findByProjectId(UUID projectId);
}
