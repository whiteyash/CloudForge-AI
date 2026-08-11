package ai.cloudforge.api.artifact;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.artifact.storage.ArtifactStorageProvider;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class ArtifactRepositoryService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactDownloadRepository downloadRepository;
    private final ArtifactStorageProvider storageProvider;
    private final EventPublisher eventPublisher;

    public ArtifactRepositoryService(
            ArtifactRepository artifactRepository,
            ArtifactDownloadRepository downloadRepository,
            ArtifactStorageProvider storageProvider,
            EventPublisher eventPublisher) {
        this.artifactRepository = artifactRepository;
        this.downloadRepository = downloadRepository;
        this.storageProvider = storageProvider;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<ArtifactResponse> getArtifactsForProject(UUID projectId) {
        return artifactRepository.findByProjectId(projectId).stream()
                .map(ArtifactResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArtifactResponse getArtifactById(UUID artifactId) {
        return artifactRepository.findById(artifactId)
                .map(ArtifactResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact not found"));
    }

    @Transactional
    public ArtifactResponse registerArtifact(
            UUID orgId,
            UUID userId,
            UUID projectId,
            UUID pipelineRunId,
            UUID jobId,
            String name,
            String artifactType,
            String version,
            String sha256Checksum,
            Long sizeBytes,
            String mimeType,
            byte[] content) {

        String storageKey = "artifacts/" + projectId + "/" + name + "-" + (version != null ? version : "1.0.0") + "." + (artifactType != null ? artifactType.toLowerCase() : "bin");
        storageProvider.storeArtifact(storageKey, content != null ? content : new byte[0]);

        Artifact artifact = artifactRepository.save(new Artifact(
                projectId, pipelineRunId, jobId, name, artifactType, version, sha256Checksum, sizeBytes, mimeType, storageProvider.getProviderName(), storageKey
        ));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ARTIFACT_CREATED",
                name + ":" + (version != null ? version : "1.0.0"),
                "Artifact " + name + " (" + artifactType + ") registered with SHA-256: " + sha256Checksum
        ));

        return ArtifactResponse.fromEntity(artifact);
    }

    @Transactional
    public byte[] downloadArtifact(UUID artifactId, String userEmail) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact not found"));

        downloadRepository.save(new ArtifactDownload(artifactId, userEmail != null ? userEmail : "system"));
        return storageProvider.retrieveArtifact(artifact.getStorageKey());
    }

    @Transactional
    public ArtifactResponse softDeleteArtifact(UUID orgId, UUID userId, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact not found"));

        artifact.setRetentionStatus("SOFT_DELETED");
        Artifact saved = artifactRepository.save(artifact);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ARTIFACT_DELETED",
                saved.getName(),
                "Artifact " + saved.getName() + " soft-deleted"
        ));

        return ArtifactResponse.fromEntity(saved);
    }

    @Transactional
    public ArtifactResponse restoreArtifact(UUID orgId, UUID userId, UUID artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ResourceNotFoundException("Artifact not found"));

        artifact.setRetentionStatus("ACTIVE");
        Artifact saved = artifactRepository.save(artifact);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ARTIFACT_RESTORED",
                saved.getName(),
                "Artifact " + saved.getName() + " restored to ACTIVE status"
        ));

        return ArtifactResponse.fromEntity(saved);
    }

    public record ArtifactResponse(
            UUID id,
            UUID projectId,
            UUID pipelineRunId,
            UUID jobId,
            String name,
            String artifactType,
            String version,
            String sha256Checksum,
            Long sizeBytes,
            String mimeType,
            String storageProvider,
            String storageKey,
            String retentionStatus
    ) {
        public static ArtifactResponse fromEntity(Artifact a) {
            return new ArtifactResponse(
                    a.getId(), a.getProjectId(), a.getPipelineRunId(), a.getJobId(),
                    a.getName(), a.getArtifactType(), a.getVersion(), a.getSha256Checksum(),
                    a.getSizeBytes(), a.getMimeType(), a.getStorageProvider(), a.getStorageKey(), a.getRetentionStatus()
            );
        }
    }
}
