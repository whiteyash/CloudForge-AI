package ai.cloudforge.api.k8s;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface K8sClusterRepository extends JpaRepository<K8sCluster, UUID> {
    List<K8sCluster> findByProjectId(UUID projectId);
    Optional<K8sCluster> findByProjectIdAndId(UUID projectId, UUID id);
    Optional<K8sCluster> findByProjectIdAndName(UUID projectId, String name);
    List<K8sCluster> findByOrganizationId(UUID organizationId);
}
