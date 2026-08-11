package ai.cloudforge.api.security;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SbomDocumentRepository extends JpaRepository<SbomDocument, UUID> {
    List<SbomDocument> findByProjectId(UUID projectId);
    Optional<SbomDocument> findByProjectIdAndId(UUID projectId, UUID id);
}
