package ai.cloudforge.api.notification;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    public EventPublisher(
            ApplicationEventPublisher eventPublisher,
            NotificationRepository notificationRepository,
            AuditLogRepository auditLogRepository) {
        this.eventPublisher = eventPublisher;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public void publishEvent(CloudForgeEvent event) {
        // 1. Audit Log Persistence
        AuditLog log = new AuditLog(event.orgId(), event.userId(), event.action(), event.target());
        auditLogRepository.save(log);

        // 2. Notification Persistence (if targeted at a user)
        if (event.userId() != null) {
            String type = "INFO";
            if ("CRITICAL".equalsIgnoreCase(event.severity())) {
                type = "CRITICAL";
            } else if ("WARN".equalsIgnoreCase(event.severity())) {
                type = "WARNING";
            }

            Notification notification = new Notification(
                    event.userId(),
                    event.action(),
                    event.message() != null ? event.message() : event.target(),
                    type,
                    null
            );
            notificationRepository.save(notification);
        }

        // 3. Spring Event Bus Broadcast
        eventPublisher.publishEvent(event);
    }
}
