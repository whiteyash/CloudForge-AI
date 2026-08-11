package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteWorkspaceRepository extends JpaRepository<FavoriteWorkspace, UUID> {

    List<FavoriteWorkspace> findByUserId(UUID userId);

    Optional<FavoriteWorkspace> findByUserIdAndOrgId(UUID userId, UUID orgId);
}
