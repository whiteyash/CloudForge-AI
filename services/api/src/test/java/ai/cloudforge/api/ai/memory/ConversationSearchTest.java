package ai.cloudforge.api.ai.memory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationSearchTest {

    private ConversationSearch search;

    @BeforeEach
    void setUp() {
        search = new ConversationSearch();
    }

    @Test
    void testSearchMemory() {
        List<String> results = search.searchMemory(List.of("Incident #101", "Pipeline Failure #44", "Deployment Success"), "incident");
        assertEquals(1, results.size());
        assertEquals("Incident #101", results.get(0));
    }
}
