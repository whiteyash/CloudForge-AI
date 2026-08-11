package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    List<Membership> findByUserId(UUID userId);

    @Query("SELECT m FROM Membership m JOIN FETCH m.user WHERE m.organization.id = :orgId")
    List<Membership> findByOrganizationIdWithUser(@Param("orgId") UUID orgId);

    List<Membership> findByOrganizationId(UUID orgId);

    @Query("SELECT m FROM Membership m WHERE m.organization.id = :orgId AND m.user.id = :userId")
    Optional<Membership> findByOrgIdAndUserId(@Param("orgId") UUID orgId, @Param("userId") UUID userId);

    boolean existsByOrganizationIdAndUserId(UUID orgId, UUID userId);
}
