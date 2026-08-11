package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnCallScheduleRepository extends JpaRepository<OnCallSchedule, UUID> {
    List<OnCallSchedule> findByOrganizationId(UUID organizationId);
}
