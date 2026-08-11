package ai.cloudforge.api.artifact;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.artifact.storage.ArtifactStorageProvider;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class ArtifactRepositoryServiceTest {

    private ArtifactRepository artifactRepository;
    private ArtifactDownloadRepository downloadRepository;
    private ArtifactStorageProvider storageProvider;
    private EventPublisher eventPublisher;
    private ArtifactRepositoryService service;

    @BeforeEach
    void setUp() {
        artifactRepository = Mockito.mock(ArtifactRepository.class);
        downloadRepository = Mockito.mock(ArtifactDownloadRepository.class);
        storageProvider = Mockito.mock(ArtifactStorageProvider.class);
        eventPublisher = Mockito.mock(EventPublisher.class);

        when(storageProvider.getProviderName()).thenReturn("LOCAL");

        service = new ArtifactRepositoryService(artifactRepository, downloadRepository, storageProvider, eventPublisher);
    }

    @Test
    void testRegisterArtifact() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(artifactRepository.save(any(Artifact.class))).thenAnswer(inv -> inv.getArgument(0));

        ArtifactRepositoryService.ArtifactResponse response = service.registerArtifact(
                orgId, userId, projectId, null, null, "core-service-jar", "JAR", "2.4.0",
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", 15420000L, "application/java-archive", "data".getBytes()
        );

        assertNotNull(response);
        assertEquals("core-service-jar", response.name());
        assertEquals("JAR", response.artifactType());
        assertEquals("2.4.0", response.version());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testSoftDeleteAndRestore() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID artifactId = UUID.randomUUID();
        Artifact artifact = new Artifact(UUID.randomUUID(), null, null, "app.zip", "ZIP", "1.0.0", "sha256", 1024L, "application/zip", "LOCAL", "key");

        when(artifactRepository.findById(artifactId)).thenReturn(Optional.of(artifact));
        when(artifactRepository.save(any(Artifact.class))).thenAnswer(inv -> inv.getArgument(0));

        ArtifactRepositoryService.ArtifactResponse deletedResponse = service.softDeleteArtifact(orgId, userId, artifactId);
        assertEquals("SOFT_DELETED", deletedResponse.retentionStatus());

        ArtifactRepositoryService.ArtifactResponse restoredResponse = service.restoreArtifact(orgId, userId, artifactId);
        assertEquals("ACTIVE", restoredResponse.retentionStatus());
    }
}
