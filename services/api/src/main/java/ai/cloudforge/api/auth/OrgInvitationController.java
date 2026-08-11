package ai.cloudforge.api.auth;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.OrgInvitationService.InvitationResponse;

@RestController
@RequestMapping
public class OrgInvitationController {

    private final OrgInvitationService invitationService;

    public OrgInvitationController(OrgInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/orgs/{orgId}/invitations")
    public ResponseEntity<List<InvitationResponse>> listInvitations(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(invitationService.listInvitations(principal.userId(), orgId));
    }

    @PostMapping("/orgs/{orgId}/invitations")
    public ResponseEntity<InvitationResponse> createInvitation(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @Valid @RequestBody CreateInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invitationService.createInvitation(principal.userId(), orgId, request.email(), request.role()));
    }

    @PostMapping("/orgs/{orgId}/invitations/{id}/resend")
    public ResponseEntity<InvitationResponse> resendInvitation(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(invitationService.resendInvitation(principal.userId(), orgId, id));
    }

    @DeleteMapping("/orgs/{orgId}/invitations/{id}")
    public ResponseEntity<Void> cancelInvitation(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID id) {
        invitationService.cancelInvitation(principal.userId(), orgId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invitations/{token}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable String token) {
        invitationService.acceptInvitation(principal.userId(), token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invitations/{token}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable String token) {
        invitationService.rejectInvitation(token);
        return ResponseEntity.noContent().build();
    }

    public record CreateInvitationRequest(
            @Email @NotNull String email,
            @NotNull Role role
    ) {}
}
