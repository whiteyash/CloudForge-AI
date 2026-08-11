package ai.cloudforge.api.git;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class RepositoryEventController {

    private final RepositoryEventService service;

    public RepositoryEventController(RepositoryEventService service) {
        this.service = service;
    }

    @PostMapping("/webhooks/{provider}")
    public ResponseEntity<RepositoryEventService.EventResponse> receiveWebhook(
            @PathVariable String provider,
            @RequestParam UUID projectId,
            @RequestParam String secret,
            @RequestHeader(value = "X-GitHub-Event", required = false, defaultValue = "push") String eventType,
            @RequestHeader(value = "X-GitHub-Delivery", required = false, defaultValue = "delivery-123") String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload) {
        return ResponseEntity.ok(service.receiveWebhook(
                projectId, provider, eventType, deliveryId, payload, signature, secret
        ));
    }

    @GetMapping("/projects/{projectId}/webhooks")
    public ResponseEntity<List<RepositoryEventService.WebhookResponse>> listWebhooks(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getWebhooksForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/webhooks")
    public ResponseEntity<RepositoryEventService.WebhookResponse> registerWebhook(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody WebhookRegistrationRequest request) {
        return ResponseEntity.ok(service.registerWebhook(
                orgId, principal.userId(), projectId, request.connectionId(),
                request.providerName(), request.targetUrl(), request.secret(), request.events()
        ));
    }

    @GetMapping("/projects/{projectId}/repository-events")
    public ResponseEntity<List<RepositoryEventService.EventResponse>> listRepositoryEvents(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getEventsForProject(projectId));
    }

    @PostMapping("/repository-events/{eventId}/replay")
    public ResponseEntity<RepositoryEventService.EventResponse> replayEvent(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam UUID orgId,
            @PathVariable UUID eventId) {
        return ResponseEntity.ok(service.replayEvent(orgId, principal.userId(), eventId));
    }

    public record WebhookRegistrationRequest(
            UUID connectionId,
            String providerName,
            String targetUrl,
            String secret,
            String events
    ) {}
}
