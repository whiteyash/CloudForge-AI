package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeGraphServiceTest {

    private KnowledgeGraphService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeGraphService();
    }

    @Test
    void testBuildKnowledgeGraph() {
        List<KnowledgeGraphService.GraphLink> links = service.buildKnowledgeGraph(UUID.randomUUID());
        assertNotNull(links);
        assertEquals(6, links.size());
        assertEquals("Incident#INC-802", links.get(0).source());
    }
}
