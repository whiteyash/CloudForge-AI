package ai.cloudforge.api.artifact;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.artifact.storage.ArtifactStorageProvider;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class ArtifactRepositorySecurityTest {

    private ArtifactRepository artifactRepository;
    private ArtifactRepositoryService service;

    @BeforeEach
    void setUp() {
        artifactRepository = Mockito.mock(ArtifactRepository.class);
        ArtifactDownloadRepository downloadRepository = Mockito.mock(ArtifactDownloadRepository.class);
        ArtifactStorageProvider storageProvider = Mockito.mock(ArtifactStorageProvider.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new ArtifactRepositoryService(artifactRepository, downloadRepository, storageProvider, eventPublisher);
    }

    @Test
    void testUnauthorizedArtifactAccessThrowsNotFound() {
        UUID artifactId = UUID.randomUUID();
        when(artifactRepository.findById(artifactId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getArtifactById(artifactId);
        });
    }
}
