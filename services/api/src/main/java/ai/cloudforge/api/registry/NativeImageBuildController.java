package ai.cloudforge.api.registry;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/{projectId}/image-builds")
public class NativeImageBuildController {

    private final NativeImageBuildService buildService;

    public NativeImageBuildController(NativeImageBuildService buildService) {
        this.buildService = buildService;
    }

    @GetMapping
    public ResponseEntity<List<NativeImageBuildDto>> getBuilds(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(buildService.getBuildsByProject(projectId, userId));
    }

    @PostMapping
    public ResponseEntity<NativeImageBuildDto> triggerBuild(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @Valid @RequestBody TriggerBuildRequest request) {
        UUID userId = (principal != null) ? principal.userId() : null;
        NativeImageBuildDto build = buildService.triggerBuild(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(build);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NativeImageBuildDto> getBuildById(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(buildService.getBuildById(projectId, id, userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<NativeImageBuildDto> cancelBuild(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(buildService.cancelBuild(projectId, id, userId));
    }
}
