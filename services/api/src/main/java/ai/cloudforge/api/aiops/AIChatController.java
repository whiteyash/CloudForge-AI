package ai.cloudforge.api.aiops;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class AIChatController {

    private final AIChatService chatService;

    public AIChatController(AIChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/projects/{projectId}/ai/chat")
    public ResponseEntity<AIChatService.ChatResponse> processChat(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.processQuery(projectId, principal.userId(), request.prompt()));
    }

    public record ChatRequest(
            String prompt
    ) {}
}
