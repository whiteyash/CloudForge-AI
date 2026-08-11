package ai.cloudforge.api.project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteProjectRepository extends JpaRepository<FavoriteProject, UUID> {

    List<FavoriteProject> findByUserId(UUID userId);

    Optional<FavoriteProject> findByUserIdAndProjectId(UUID userId, UUID projectId);
}
