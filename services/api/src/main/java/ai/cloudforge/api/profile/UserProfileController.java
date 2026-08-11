package ai.cloudforge.api.profile;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.profile.UserProfileService.ChangePasswordRequest;
import ai.cloudforge.api.profile.UserProfileService.UpdateProfileRequest;
import ai.cloudforge.api.profile.UserProfileService.UserProfileResponse;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal AuthPrincipal principal) {
        return ResponseEntity.ok(userProfileService.getProfile(principal.userId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(principal.userId(), request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userProfileService.changePassword(principal.userId(), request);
        return ResponseEntity.noContent().build();
    }
}
