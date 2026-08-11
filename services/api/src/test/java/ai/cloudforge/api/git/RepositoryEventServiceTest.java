package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class RepositoryEventServiceTest {

    private RepositoryWebhookRepository webhookRepository;
    private RepositoryEventRepository eventRepository;
    private EventPublisher eventPublisher;
    private RepositoryEventService service;

    @BeforeEach
    void setUp() {
        webhookRepository = Mockito.mock(RepositoryWebhookRepository.class);
        eventRepository = Mockito.mock(RepositoryEventRepository.class);
        WebhookSignatureValidator signatureValidator = new WebhookSignatureValidator();
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositoryEventService(webhookRepository, eventRepository, signatureValidator, eventPublisher);
    }

    @Test
    void testRegisterWebhook() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(webhookRepository.save(any(RepositoryWebhook.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositoryEventService.WebhookResponse response = service.registerWebhook(
                orgId, userId, projectId, null, "GITHUB", "https://api.cloudforge.ai/webhooks/github", "secret123", "push,pull_request"
        );

        assertNotNull(response);
        assertEquals("GITHUB", response.providerName());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testReplayEvent() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        RepositoryEvent event = new RepositoryEvent(UUID.randomUUID(), null, "GITHUB", "push", "del-123", "hash123");
        event.setStatus("FAILED");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(RepositoryEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositoryEventService.EventResponse response = service.replayEvent(orgId, userId, eventId);

        assertNotNull(response);
        assertEquals("PROCESSED", response.status());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
