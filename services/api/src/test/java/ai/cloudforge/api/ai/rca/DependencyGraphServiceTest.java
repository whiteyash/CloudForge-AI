package ai.cloudforge.api.ai.rca;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DependencyGraphServiceTest {

    private DependencyGraphService graphService;

    @BeforeEach
    void setUp() {
        graphService = new DependencyGraphService();
    }

    @Test
    void testBuildGraphStructure() {
        List<DependencyGraphService.DependencyLinkResponse> graph = graphService.buildGraph(UUID.randomUUID());
        assertNotNull(graph);
        assertEquals(6, graph.size());
        assertEquals("Pipeline#44", graph.get(0).sourceComponent());
    }
}
