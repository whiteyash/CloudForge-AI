package ai.cloudforge.api.ai.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextBuilderTest {

    private ContextBuilder contextBuilder;

    @BeforeEach
    void setUp() {
        contextBuilder = new ContextBuilder();
    }

    @Test
    void testBuildContext() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ContextBuilder.OperationalContext context = contextBuilder.buildContext(projectId, userId, "PIPELINE");

        assertNotNull(context);
        assertEquals("PIPELINE", context.targetEntity());
        assertEquals("HEALTHY", context.environmentHealth());
    }
}
