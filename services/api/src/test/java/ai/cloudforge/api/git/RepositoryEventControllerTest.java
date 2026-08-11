package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.cloudforge.api.auth.AuthPrincipal;

class RepositoryEventControllerTest {

    private RepositoryEventService service;
    private RepositoryEventController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(RepositoryEventService.class);
        controller = new RepositoryEventController(service);
    }

    @Test
    void testReceiveWebhookEndpoint() {
        UUID projectId = UUID.randomUUID();
        String payload = "{\"ref\":\"refs/heads/main\"}";

        when(service.receiveWebhook(projectId, "GITHUB", "push", "delivery-1", payload, null, "secret"))
                .thenReturn(new RepositoryEventService.EventResponse(
                        UUID.randomUUID(), projectId, null, "GITHUB", "push", "delivery-1", UUID.randomUUID(), "PROCESSED", null
                ));

        ResponseEntity<RepositoryEventService.EventResponse> response = controller.receiveWebhook(
                "GITHUB", projectId, "secret", "push", "delivery-1", null, payload
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("push", response.getBody().eventType());
    }

    @Test
    void testListWebhooksEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getWebhooksForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<RepositoryEventService.WebhookResponse>> response = controller.listWebhooks(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testRegisterWebhookEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        RepositoryEventController.WebhookRegistrationRequest request = new RepositoryEventController.WebhookRegistrationRequest(
                null, "GITHUB", "https://api.cloudforge.ai/webhooks/github", "secret123", "push,pull_request"
        );

        when(service.registerWebhook(orgId, userId, projectId, null, "GITHUB", "https://api.cloudforge.ai/webhooks/github", "secret123", "push,pull_request"))
                .thenReturn(new RepositoryEventService.WebhookResponse(
                        UUID.randomUUID(), projectId, null, "GITHUB", "https://api.cloudforge.ai/webhooks/github", "push,pull_request", "ACTIVE"
                ));

        ResponseEntity<RepositoryEventService.WebhookResponse> response = controller.registerWebhook(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testReplayEventEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        when(service.replayEvent(orgId, userId, eventId))
                .thenReturn(new RepositoryEventService.EventResponse(
                        eventId, UUID.randomUUID(), null, "GITHUB", "push", "del-1", UUID.randomUUID(), "PROCESSED", null
                ));

        ResponseEntity<RepositoryEventService.EventResponse> response = controller.replayEvent(principal, orgId, eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
