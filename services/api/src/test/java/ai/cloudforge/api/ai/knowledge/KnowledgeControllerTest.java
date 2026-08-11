package ai.cloudforge.api.ai.knowledge;

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

class KnowledgeControllerTest {

    private KnowledgeService service;
    private RunbookRecommendationService recommendationService;
    private KnowledgeController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(KnowledgeService.class);
        recommendationService = Mockito.mock(RunbookRecommendationService.class);
        controller = new KnowledgeController(service, recommendationService);
    }

    @Test
    void testSearchKnowledgeEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.searchKnowledge(projectId, "Postgres")).thenReturn(List.of());

        ResponseEntity<List<KnowledgeSearchService.SearchResult>> response = controller.searchKnowledge(projectId, "Postgres");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testListRunbooksEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getRunbooksForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<KnowledgeService.AIRunbookResponse>> response = controller.listRunbooks(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
