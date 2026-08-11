package ai.cloudforge.api.auth;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.UserPreferencesService.UpdateNotificationPreferencesRequest;
import ai.cloudforge.api.auth.UserPreferencesService.UpdatePreferencesRequest;

@RestController
@RequestMapping("/profile")
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;

    public UserPreferencesController(UserPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferences> getPreferences(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(preferencesService.getPreferences(principal.userId()));
    }

    @PatchMapping("/preferences")
    public ResponseEntity<UserPreferences> updatePreferences(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody UpdatePreferencesRequest request) {
        return ResponseEntity.ok(preferencesService.updatePreferences(principal.userId(), request));
    }

    @GetMapping("/notification-preferences")
    public ResponseEntity<NotificationPreferences> getNotificationPreferences(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(preferencesService.getNotificationPreferences(principal.userId()));
    }

    @PatchMapping("/notification-preferences")
    public ResponseEntity<NotificationPreferences> updateNotificationPreferences(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody UpdateNotificationPreferencesRequest request) {
        return ResponseEntity.ok(preferencesService.updateNotificationPreferences(principal.userId(), request));
    }

    @GetMapping("/personal-audit")
    public ResponseEntity<List<AuditLog>> getPersonalAuditTrail(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(preferencesService.getPersonalAuditTrail(principal.userId()));
    }
}
