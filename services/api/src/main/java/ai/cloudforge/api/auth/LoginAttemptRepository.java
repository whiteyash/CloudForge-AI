package ai.cloudforge.api.auth;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    long countByEmailAndSuccessFalse(String email);
}
