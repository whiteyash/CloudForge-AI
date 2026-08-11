package ai.cloudforge.api.k8s;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelmReleaseRepository extends JpaRepository<HelmRelease, UUID> {
    List<HelmRelease> findByProjectId(UUID projectId);
    List<HelmRelease> findByClusterId(UUID clusterId);
    Optional<HelmRelease> findByClusterIdAndReleaseName(UUID clusterId, String releaseName);
}
