package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveSessionRepository extends JpaRepository<ActiveSession, UUID> {

    List<ActiveSession> findByUserIdOrderByLastActiveAtDesc(UUID userId);

    Optional<ActiveSession> findByIdAndUserId(UUID id, UUID userId);

    Optional<ActiveSession> findBySessionToken(String sessionToken);

    @Modifying
    @Query("DELETE FROM ActiveSession s WHERE s.user.id = :userId")
    void deleteAllForUser(UUID userId);
}
