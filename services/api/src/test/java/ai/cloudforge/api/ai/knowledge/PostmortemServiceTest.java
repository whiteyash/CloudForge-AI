package ai.cloudforge.api.ai.knowledge;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostmortemServiceTest {

    private PostmortemService service;

    @BeforeEach
    void setUp() {
        service = new PostmortemService();
    }

    @Test
    void testGeneratePostmortem() {
        PostmortemService.GeneratedPostmortem pm = service.generatePostmortem(UUID.randomUUID(), UUID.randomUUID());
        assertNotNull(pm);
        assertTrue(pm.summary().contains("Executive Postmortem Report"));
    }
}
