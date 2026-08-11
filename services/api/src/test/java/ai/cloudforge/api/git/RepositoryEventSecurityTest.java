package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class RepositoryEventSecurityTest {

    private RepositoryEventRepository eventRepository;
    private WebhookSignatureValidator signatureValidator;
    private RepositoryEventService service;

    @BeforeEach
    void setUp() {
        RepositoryWebhookRepository webhookRepository = Mockito.mock(RepositoryWebhookRepository.class);
        eventRepository = Mockito.mock(RepositoryEventRepository.class);
        signatureValidator = new WebhookSignatureValidator();
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

        service = new RepositoryEventService(webhookRepository, eventRepository, signatureValidator, eventPublisher);
    }

    @Test
    void testInvalidSignatureMarkedAsFailed() {
        UUID projectId = UUID.randomUUID();
        String payload = "{\"action\":\"closed\"}";

        when(eventRepository.save(Mockito.any(RepositoryEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RepositoryEventService.EventResponse response = service.receiveWebhook(
                projectId, "GITHUB", "pull_request", "delivery-99", payload, "sha256=invalid_signature", "secret123"
        );

        assertEquals("FAILED", response.status());
        assertEquals("Invalid HMAC signature", response.failureReason());
    }

    @Test
    void testUnauthorizedEventReplayThrowsNotFound() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.replayEvent(orgId, userId, eventId);
        });
    }
}
