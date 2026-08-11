package ai.cloudforge.api.auth;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRoleHistoryRepository extends JpaRepository<MembershipRoleHistory, UUID> {

    List<MembershipRoleHistory> findByOrgIdOrderByCreatedAtDesc(UUID orgId);
}
