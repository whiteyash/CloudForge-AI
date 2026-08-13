package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.ai.memory.AIConversationResponse;
import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class CopilotController {

    private final CopilotService service;
    private final ExecutiveBriefService briefService;

    public CopilotController(CopilotService service, ExecutiveBriefService briefService) {
        this.service = service;
        this.briefService = briefService;
    }

    @PostMapping("/projects/{projectId}/copilot/chat")
    public ResponseEntity<AIConversationResponse<CopilotService.CopilotResponse>> chat(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID conversationId,
            @org.springframework.web.bind.annotation.RequestHeader(name = "X-CloudForge-Environment", required = false, defaultValue = "DEV") String environment,
            @RequestParam String prompt) {
        return ResponseEntity.ok(service.processCopilotChat(orgId, principal.userId(), projectId, conversationId, prompt, environment));
    }

    @GetMapping("/projects/{projectId}/copilot/conversations")
    public ResponseEntity<List<CopilotService.CopilotSessionResponse>> getConversations(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getConversationsForProject(projectId));
    }

    @GetMapping("/projects/{projectId}/copilot/conversations/{conversationId}")
    public ResponseEntity<List<CopilotService.CopilotSessionResponse>> getConversation(
            @PathVariable UUID projectId,
            @PathVariable UUID conversationId) {
        return ResponseEntity.ok(service.getConversationsForProject(projectId));
    }

    @DeleteMapping("/projects/{projectId}/copilot/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable UUID projectId,
            @PathVariable UUID conversationId) {
        service.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/copilot/executive-brief")
    public ResponseEntity<AIConversationResponse<ExecutiveBriefService.ExecutiveBriefResponse>> getExecutiveBrief(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "DAILY") String periodType) {
        return ResponseEntity.ok(briefService.generateExecutiveBrief(projectId, principal.userId(), periodType));
    }
}
