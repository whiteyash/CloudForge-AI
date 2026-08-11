package ai.cloudforge.api.registry;

import java.util.List;
import java.util.UUID;

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

import ai.cloudforge.api.auth.AuthPrincipal;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/{projectId}")
public class ContainerRegistryController {

    private final ContainerRegistryService registryService;

    public ContainerRegistryController(ContainerRegistryService registryService) {
        this.registryService = registryService;
    }

    @GetMapping("/registries")
    public ResponseEntity<List<ContainerRegistryDto>> getRegistries(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.getRegistriesByProject(projectId, userId));
    }

    @PostMapping("/registries")
    public ResponseEntity<ContainerRegistryDto> connectRegistry(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRegistryRequest request) {
        UUID userId = (principal != null) ? principal.userId() : null;
        ContainerRegistryDto created = registryService.connectRegistry(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/registries/{id}")
    public ResponseEntity<ContainerRegistryDto> getRegistryById(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.getRegistryById(projectId, id, userId));
    }

    @PostMapping("/registries/{id}/test")
    public ResponseEntity<ContainerRegistryDto> testConnection(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.testConnection(projectId, id, userId));
    }

    @PostMapping("/registries/{id}/sync")
    public ResponseEntity<RegistrySyncResultDto> syncRegistry(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.syncRegistry(projectId, id, userId));
    }

    @DeleteMapping("/registries/{id}")
    public ResponseEntity<Void> disconnectRegistry(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        registryService.disconnectRegistry(projectId, id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registries/{id}/repositories")
    public ResponseEntity<List<ContainerImageRepositoryDto>> getImageRepositories(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.getImageRepositories(projectId, id, userId));
    }

    @GetMapping("/registries/{id}/repositories/{repoId}/tags")
    public ResponseEntity<List<ContainerImageTagDto>> getImageTags(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @PathVariable UUID repoId) {
        UUID userId = (principal != null) ? principal.userId() : null;
        return ResponseEntity.ok(registryService.getImageTags(projectId, id, repoId, userId));
    }

    @DeleteMapping("/registries/{id}/repositories/{repoId}/tags/{tagId}")
    public ResponseEntity<Void> deleteImageTag(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @PathVariable UUID repoId,
            @PathVariable UUID tagId) {
        UUID userId = (principal != null) ? principal.userId() : null;
        registryService.deleteImageTag(projectId, id, repoId, tagId, userId);
        return ResponseEntity.noContent().build();
    }
}
