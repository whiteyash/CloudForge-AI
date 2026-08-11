package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeSearchServiceTest {

    private KnowledgeSearchService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeSearchService();
    }

    @Test
    void testSearchKnowledge() {
        List<KnowledgeSearchService.SearchResult> results = service.searchKnowledge(UUID.randomUUID(), "Postgres");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("RUNBOOK", results.get(0).type());
    }
}
