package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgInvitationRepository extends JpaRepository<OrgInvitation, UUID> {

    Optional<OrgInvitation> findByToken(String token);

    List<OrgInvitation> findByOrganizationIdOrderByCreatedAtDesc(UUID orgId);

    List<OrgInvitation> findByOrganizationIdAndStatus(UUID orgId, String status);

    Optional<OrgInvitation> findByOrganizationIdAndEmailIgnoreCaseAndStatus(UUID orgId, String email, String status);
}
