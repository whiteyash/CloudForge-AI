package ai.cloudforge.api.auth;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    java.util.List<AuditLog> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
}
