package ai.cloudforge.api.git;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitProviderConnectionRepository extends JpaRepository<GitProviderConnection, UUID> {

    List<GitProviderConnection> findByOrgId(UUID orgId);

    Optional<GitProviderConnection> findByOrgIdAndProviderNameAndAccountName(UUID orgId, String providerName, String accountName);
}
