package ai.cloudforge.api.k8s;

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
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.k8s.K8sDtos.ClusterResponseDto;
import ai.cloudforge.api.k8s.K8sDtos.ConnectClusterRequest;
import ai.cloudforge.api.k8s.K8sDtos.DeployHelmReleaseRequest;
import ai.cloudforge.api.k8s.K8sDtos.GitOpsSyncConfigDto;
import ai.cloudforge.api.k8s.K8sDtos.HelmReleaseDto;
import ai.cloudforge.api.k8s.K8sDtos.K8sClusterSummaryDto;
import ai.cloudforge.api.k8s.K8sDtos.SyncGitOpsRequest;
import jakarta.validation.Valid;

@RestController
public class K8sClusterController {

    private final K8sClusterService k8sClusterService;

    public K8sClusterController(K8sClusterService k8sClusterService) {
        this.k8sClusterService = k8sClusterService;
    }

    @GetMapping("/k8s/summary")
    public ResponseEntity<K8sClusterSummaryDto> getSummary(@AuthenticationPrincipal AuthPrincipal principal) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.getClusterSummary(null, userId));
    }

    @GetMapping("/projects/{projectId}/clusters/summary")
    public ResponseEntity<K8sClusterSummaryDto> getProjectSummary(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.getClusterSummary(projectId, userId));
    }

    @GetMapping("/projects/{projectId}/clusters")
    public ResponseEntity<List<ClusterResponseDto>> listClusters(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.listClusters(projectId, userId));
    }

    @PostMapping("/projects/{projectId}/clusters")
    public ResponseEntity<ClusterResponseDto> connectCluster(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @Valid @RequestBody ConnectClusterRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        ClusterResponseDto created = k8sClusterService.connectCluster(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/projects/{projectId}/clusters/{id}")
    public ResponseEntity<ClusterResponseDto> getClusterById(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.getCluster(projectId, id, userId));
    }

    @DeleteMapping("/projects/{projectId}/clusters/{id}")
    public ResponseEntity<Void> disconnectCluster(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.userId() : null;
        k8sClusterService.disconnectCluster(projectId, id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/projects/{projectId}/clusters/{id}/sync")
    public ResponseEntity<ClusterResponseDto> syncClusterHealth(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.syncClusterHealth(projectId, id, userId));
    }

    @PostMapping("/projects/{projectId}/clusters/{id}/helm/deploy")
    public ResponseEntity<HelmReleaseDto> deployHelmRelease(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody DeployHelmReleaseRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        HelmReleaseDto deployed = k8sClusterService.deployHelmRelease(projectId, id, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(deployed);
    }

    @GetMapping("/projects/{projectId}/clusters/{id}/helm/releases")
    public ResponseEntity<List<HelmReleaseDto>> listHelmReleases(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.listHelmReleases(projectId, id, userId));
    }

    @DeleteMapping("/projects/{projectId}/clusters/{id}/helm/releases/{releaseId}")
    public ResponseEntity<Void> uninstallHelmRelease(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @PathVariable UUID releaseId) {
        UUID userId = principal != null ? principal.userId() : null;
        k8sClusterService.uninstallHelmRelease(projectId, id, releaseId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/projects/{projectId}/clusters/{id}/gitops/sync")
    public ResponseEntity<GitOpsSyncConfigDto> syncGitOps(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody SyncGitOpsRequest request) {
        UUID userId = principal != null ? principal.userId() : null;
        return ResponseEntity.ok(k8sClusterService.syncGitOps(projectId, id, request, userId));
    }
}
