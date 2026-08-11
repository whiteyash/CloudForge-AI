package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class RepositoryEventService {

    private final RepositoryWebhookRepository webhookRepository;
    private final RepositoryEventRepository eventRepository;
    private final WebhookSignatureValidator signatureValidator;
    private final EventPublisher eventPublisher;

    public RepositoryEventService(
            RepositoryWebhookRepository webhookRepository,
            RepositoryEventRepository eventRepository,
            WebhookSignatureValidator signatureValidator,
            EventPublisher eventPublisher) {
        this.webhookRepository = webhookRepository;
        this.eventRepository = eventRepository;
        this.signatureValidator = signatureValidator;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<WebhookResponse> getWebhooksForProject(UUID projectId) {
        return webhookRepository.findByProjectId(projectId).stream()
                .map(WebhookResponse::fromEntity)
                .toList();
    }

    @Transactional
    public WebhookResponse registerWebhook(UUID orgId, UUID userId, UUID projectId, UUID connectionId, String providerName, String targetUrl, String secret, String events) {
        RepositoryWebhook webhook = webhookRepository.save(new RepositoryWebhook(
                projectId, connectionId, providerName, targetUrl, secret, events
        ));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "WEBHOOK_REGISTERED",
                targetUrl,
                "Git webhook registered for provider " + providerName
        ));

        return WebhookResponse.fromEntity(webhook);
    }

    @Transactional
    public EventResponse receiveWebhook(UUID projectId, String providerName, String eventType, String deliveryId, String payload, String signature, String secret) {
        boolean valid = signatureValidator.validateSignature(payload, signature, secret, providerName);

        String payloadHash = Integer.toHexString(payload.hashCode());
        RepositoryEvent event = new RepositoryEvent(projectId, null, providerName, eventType, deliveryId, payloadHash);

        if (!valid) {
            event.setStatus("FAILED");
            event.setFailureReason("Invalid HMAC signature");
        } else {
            event.setStatus("PROCESSED");
        }

        RepositoryEvent saved = eventRepository.save(event);

        eventPublisher.publishEvent(new CloudForgeEvent(
                projectId,
                UUID.randomUUID(),
                "WEBHOOK_RECEIVED",
                eventType,
                "Webhook event " + eventType + " received with status " + saved.getStatus()
        ));

        return EventResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsForProject(UUID projectId) {
        return eventRepository.findByProjectIdOrderByReceivedAtDesc(projectId).stream()
                .map(EventResponse::fromEntity)
                .toList();
    }

    @Transactional
    public EventResponse replayEvent(UUID orgId, UUID userId, UUID eventId) {
        RepositoryEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository event not found"));

        event.setStatus("PROCESSED");
        event.setFailureReason(null);
        RepositoryEvent saved = eventRepository.save(event);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "EVENT_REPLAYED",
                saved.getEventType(),
                "Repository event " + saved.getEventType() + " replayed successfully"
        ));

        return EventResponse.fromEntity(saved);
    }

    public record WebhookResponse(
            UUID id,
            UUID projectId,
            UUID connectionId,
            String providerName,
            String targetUrl,
            String events,
            String status
    ) {
        public static WebhookResponse fromEntity(RepositoryWebhook w) {
            return new WebhookResponse(w.getId(), w.getProjectId(), w.getConnectionId(), w.getProviderName(), w.getTargetUrl(), w.getEvents(), w.getStatus());
        }
    }

    public record EventResponse(
            UUID id,
            UUID projectId,
            UUID repositoryId,
            String providerName,
            String eventType,
            String deliveryId,
            UUID correlationId,
            String status,
            String failureReason
    ) {
        public static EventResponse fromEntity(RepositoryEvent e) {
            return new EventResponse(e.getId(), e.getProjectId(), e.getRepositoryId(), e.getProviderName(), e.getEventType(), e.getDeliveryId(), e.getCorrelationId(), e.getStatus(), e.getFailureReason());
        }
    }
}
