package ai.cloudforge.api.artifact;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class ArtifactRepositoryController {

    private final ArtifactRepositoryService service;

    public ArtifactRepositoryController(ArtifactRepositoryService service) {
        this.service = service;
    }

    @GetMapping("/projects/{projectId}/artifacts")
    public ResponseEntity<List<ArtifactRepositoryService.ArtifactResponse>> listArtifacts(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getArtifactsForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/artifacts")
    public ResponseEntity<ArtifactRepositoryService.ArtifactResponse> registerArtifact(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestBody RegisterArtifactRequest request) {
        return ResponseEntity.ok(service.registerArtifact(
                orgId, principal.userId(), projectId, request.pipelineRunId(), request.jobId(),
                request.name(), request.artifactType(), request.version(), request.sha256Checksum(),
                request.sizeBytes(), request.mimeType(), request.content()
        ));
    }

    @GetMapping("/artifacts/{artifactId}")
    public ResponseEntity<ArtifactRepositoryService.ArtifactResponse> getArtifactById(
            @PathVariable UUID artifactId) {
        return ResponseEntity.ok(service.getArtifactById(artifactId));
    }

    @GetMapping("/artifacts/{artifactId}/download")
    public ResponseEntity<byte[]> downloadArtifact(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID artifactId) {
        byte[] data = service.downloadArtifact(artifactId, principal.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"artifact.bin\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/artifacts/{artifactId}")
    public ResponseEntity<ArtifactRepositoryService.ArtifactResponse> softDeleteArtifact(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID artifactId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.softDeleteArtifact(orgId, principal.userId(), artifactId));
    }

    @PostMapping("/artifacts/{artifactId}/restore")
    public ResponseEntity<ArtifactRepositoryService.ArtifactResponse> restoreArtifact(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID artifactId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.restoreArtifact(orgId, principal.userId(), artifactId));
    }

    public record RegisterArtifactRequest(
            UUID pipelineRunId,
            UUID jobId,
            String name,
            String artifactType,
            String version,
            String sha256Checksum,
            Long sizeBytes,
            String mimeType,
            byte[] content
    ) {}
}
