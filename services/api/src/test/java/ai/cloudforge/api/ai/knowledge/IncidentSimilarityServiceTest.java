package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IncidentSimilarityServiceTest {

    private IncidentSimilarityService service;

    @BeforeEach
    void setUp() {
        service = new IncidentSimilarityService();
    }

    @Test
    void testFindSimilarIncidents() {
        List<IncidentSimilarityService.SimilarIncidentResult> results = service.findSimilarIncidents(UUID.randomUUID(), UUID.randomUUID());
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(94, results.get(0).similarityPercent());
    }
}
