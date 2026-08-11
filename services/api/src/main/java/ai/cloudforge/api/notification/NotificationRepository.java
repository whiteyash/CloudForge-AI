package ai.cloudforge.api.notification;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserIdAndStatus(UUID userId, String status);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ' WHERE n.userId = :userId AND n.status = 'UNREAD'")
    void markAllAsReadForUser(UUID userId);
}
