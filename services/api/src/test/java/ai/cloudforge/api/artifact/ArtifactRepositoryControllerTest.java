package ai.cloudforge.api.artifact;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ai.cloudforge.api.auth.AuthPrincipal;

class ArtifactRepositoryControllerTest {

    private ArtifactRepositoryService service;
    private ArtifactRepositoryController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(ArtifactRepositoryService.class);
        controller = new ArtifactRepositoryController(service);
    }

    @Test
    void testListArtifactsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getArtifactsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<ArtifactRepositoryService.ArtifactResponse>> response = controller.listArtifacts(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testRegisterArtifactEndpoint() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, "test@cloudforge.ai");

        ArtifactRepositoryController.RegisterArtifactRequest request = new ArtifactRepositoryController.RegisterArtifactRequest(
                null, null, "service.jar", "JAR", "1.0.0", "sha256hash", 5000L, "application/java-archive", "data".getBytes()
        );

        when(service.registerArtifact(orgId, userId, projectId, null, null, "service.jar", "JAR", "1.0.0", "sha256hash", 5000L, "application/java-archive", request.content()))
                .thenReturn(new ArtifactRepositoryService.ArtifactResponse(
                        UUID.randomUUID(), projectId, null, null, "service.jar", "JAR", "1.0.0", "sha256hash", 5000L, "application/java-archive", "LOCAL", "key", "ACTIVE"
                ));

        ResponseEntity<ArtifactRepositoryService.ArtifactResponse> response = controller.registerArtifact(principal, projectId, orgId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("service.jar", response.getBody().name());
    }
}
