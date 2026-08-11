package ai.cloudforge.api.k8s;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitOpsSyncConfigRepository extends JpaRepository<GitOpsSyncConfig, UUID> {
    List<GitOpsSyncConfig> findByProjectId(UUID projectId);
    List<GitOpsSyncConfig> findByClusterId(UUID clusterId);
    Optional<GitOpsSyncConfig> findByClusterIdAndRepoUrl(UUID clusterId, String repoUrl);
}
