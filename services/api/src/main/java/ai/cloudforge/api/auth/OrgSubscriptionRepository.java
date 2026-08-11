package ai.cloudforge.api.auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgSubscriptionRepository extends JpaRepository<OrgSubscription, UUID> {

    Optional<OrgSubscription> findByOrganizationId(UUID orgId);
}
